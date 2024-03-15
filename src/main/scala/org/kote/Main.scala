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
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.domain.comment.Comment
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.content.file.File
import org.kote.domain.content.file.File.{FileId, fileParser}
import org.kote.domain.group.Group
import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task
import org.kote.domain.task.Task.TaskId
import org.kote.domain.user.User
import org.kote.domain.user.User.UserId
import org.kote.repository._
import org.kote.service._
import org.kote.service.notion.v1.PropertyId
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import java.nio.charset.StandardCharsets

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val ioConfig = IO.delay(ConfigSource.default.loadOrThrow[AppConfig])

    ioConfig.flatMap(config => makeTransactor[IO](config.database).use {
      _: Transactor[IO] =>
        for {
          _ <- FlywayMigration.migrate[IO](config.database)

          userCache <- Cache.ram[IO, UserId, User]
          taskCache <- Cache.ram[IO, TaskId, Task]
          groupCache <- Cache.ram[IO, GroupId, Group]
          boardCache <- Cache.ram[IO, BoardId, Board]
          commentCache <- Cache.ram[IO, CommentId, Comment]
          fileCache <- Cache.disk[IO, FileId, File]("files", StandardCharsets.UTF_8)

          boardDb <- Cache.ram[IO, BoardId, NotionDatabaseId]
          dbBoard <- Cache.ram[IO, NotionDatabaseId, BoardId]

          userPage <- Cache.ram[IO, UserId, NotionPageId]
          pageUser <- Cache.ram[IO, NotionPageId, UserId]

          userToNotionUser <- Cache.ram[IO, UserId, NotionUserId]
          notionUserToUser <- Cache.ram[IO, NotionUserId, UserId]

          groupProperty <- Cache.ram[IO, GroupId, PropertyId]
          propertyGroup <- Cache.ram[IO, PropertyId, GroupId]

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

            val userRepo = UserRepository.inMemory(userCache)
            val taskRepo = TaskRepository.inMemory(taskCache)
            val groupRepo = GroupRepository.inMemory(groupCache)
            val boardRepo = BoardRepository.inMemory(boardCache)
            val commentRepo = CommentRepository.inMemory(commentCache)
            val fileRepo = FileRepository.inMemory(fileCache)

            val databaseIntegration = IntegrationRepository.inMemory(boardDb, dbBoard)
            val userMainPageIntegration = IntegrationRepository.inMemory(userPage, pageUser)
            val userToNotionUserIntegration =
              IntegrationRepository.inMemory(userToNotionUser, notionUserToUser)
            val groupToPropertyIntegration =
              IntegrationRepository.inMemory(groupProperty, propertyGroup)

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
              TaskController.make(TaskService.fromRepository(boardRepo, groupRepo, taskRepo)),
              GroupController.make(GroupService.fromRepository(boardRepo, groupRepo, taskRepo)),
              BoardController.make(
                BoardService.syncNotion(
                  boardRepo,
                  groupRepo,
                  taskRepo,
                  notionDatabaseClient,
                  databaseIntegration,
                  userMainPageIntegration,
                  userToNotionUserIntegration,
                  groupToPropertyIntegration,
                ),
              ),
              CommentController.make(CommentService.fromRepository(taskRepo, commentRepo)),
              FileController.make(FileService.fromRepository(taskRepo, fileRepo)),
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
    })
  }
}
