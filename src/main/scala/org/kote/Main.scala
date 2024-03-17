package org.kote

import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{Host, Port}
import doobie.util.transactor.Transactor
import org.asynchttpclient.DefaultAsyncHttpClient
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.kote.client.notion._
import org.kote.common.cache.Cache
import org.kote.config.AppConfig
import org.kote.controller._
import org.kote.database.FlywayMigration
import org.kote.database.transactor.makeTransactor
import org.kote.domain.comment.Comment
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.task.Task
import org.kote.domain.task.Task.TaskId
import org.kote.repository._
import org.kote.repository.postgresql.integration.notion.{NotionDatabaseIntegrationRepositoryPostgresql, NotionMainPageIntegrationRepositoryPostgresql, NotionUserIntegrationRepositoryPostgresql}
import org.kote.service._
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val ioConfig = IO.delay(ConfigSource.default.loadOrThrow[AppConfig])

    ioConfig.flatMap(config =>
      makeTransactor[IO](config.database).use { implicit tr: Transactor[IO] =>
        for {
          _ <- FlywayMigration.migrate[IO](config.database)
          taskCache <- Cache.ram[IO, TaskId, Task]
          commentCache <- Cache.ram[IO, CommentId, Comment]

          endpoints <- IO.delay {
            val backend: SttpBackend[IO, Any] =
              AsyncHttpClientCatsBackend.usingClient[IO](new DefaultAsyncHttpClient)

            val notionConfig = config.notion.toNotionClientConfiguration

            val notionDatabaseClient: NotionDatabaseClient[IO] =
              NotionDatabaseClient.http(backend, notionConfig)

            val notionPageClient: NotionPageClient[IO] =
              NotionPageClient.http(backend, notionConfig)

            val notionUserClient: NotionUserClient[IO] =
              NotionUserClient.http(backend, notionConfig)

            val userRepo = UserRepository.postgres[IO]
            val taskRepo = TaskRepository.inMemory(taskCache)
            val groupRepo = GroupRepository.postgres[IO]
            val boardRepo = BoardRepository.postgres[IO]
            val commentRepo = CommentRepository.inMemory(commentCache)

            val databaseIntegration = new NotionDatabaseIntegrationRepositoryPostgresql[IO]
            val userMainPageIntegration = new NotionMainPageIntegrationRepositoryPostgresql[IO]
            val userToNotionUserIntegration = new NotionUserIntegrationRepositoryPostgresql[IO]

            List(
              UserController.make(
                UserService.syncNotion(
                  userRepo,
                  boardRepo,
                  groupRepo,
                  taskRepo,
                  notionUserClient,
                  notionPageClient,
                  userToNotionUserIntegration,
                  userMainPageIntegration,
                ),
              ),
              TaskController.make(TaskService.fromRepository(taskRepo)),
              GroupController.make(GroupService.fromRepository(groupRepo, taskRepo)),
              BoardController.make(
                BoardService.syncNotion(
                  boardRepo,
                  groupRepo,
                  taskRepo,
                  notionDatabaseClient,
                  databaseIntegration,
                  userMainPageIntegration,
                  userToNotionUserIntegration,
                ),
              ),
              CommentController.make(CommentService.fromRepository(commentRepo)),
            ).flatMap(_.endpoints)
          }
          swagger = SwaggerInterpreter().fromServerEndpoints[IO](endpoints, "my-notes", "1.0.0")
          routes = Http4sServerInterpreter[IO]().toRoutes(swagger ++ endpoints)
          port <- IO.fromOption(Port.fromInt(config.http.port))(
            new IllegalArgumentException("Invalid http port"),
          )
          _ <- EmberServerBuilder
            .default[IO]
            .withHost(Host.fromString("localhost").get)
            .withPort(port)
            .withHttpApp(Router("/" -> routes).orNotFound)
            .build
            .use { server =>
              for {
                _ <- IO.println(
                  s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit.",
                )
                _ <- IO.readLine
              } yield ()
            }
        } yield ()
      },
    )
  }
}
