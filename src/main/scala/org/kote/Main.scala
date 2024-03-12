package org.kote

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.std.Env
import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{Host, Port}
import org.asynchttpclient.DefaultAsyncHttpClient
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion._
import org.kote.common.cache.Cache
import org.kote.controller._
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
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import java.nio.charset.StandardCharsets
import scala.concurrent.duration.DurationInt

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
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

      notionApiKey <- getNotionApiKey[IO].orElse(IO.pure(""))
      endpoints <- IO.delay {
        val backend: SttpBackend[IO, Any] =
          AsyncHttpClientCatsBackend.usingClient[IO](new DefaultAsyncHttpClient)

        val config = NotionConfiguration(
          apiKey = notionApiKey,
          notionVersion = "2022-06-28",
          url = "https://api.notion.com",
          timeout = 10.seconds,
        )

        val notionDatabaseClient: NotionDatabaseClient[IO] =
          NotionDatabaseClient.http(backend, config)

        val notionPageClient: NotionPageClient[IO] =
          NotionPageClient.http(backend, config)

        val notionUserClient: NotionUserClient[IO] =
          NotionUserClient.http(backend, config)

        val userRepo = UserRepository.inMemory(userCache)
        val taskRepo = TaskRepository.inMemory(taskCache)
        val groupRepo = GroupRepository.inMemory(groupCache)
        val boardRepo =
          BoardRepository.mix(
            BoardRepository.inMemory(boardCache),
          )
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
      port <- getPort[IO]
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
    } yield ExitCode.Success

  private def getPort[F[_]: Env: MonadThrow]: F[Port] =
    OptionT(Env[F].get("HTTP_PORT"))
      .toRight("HTTP_PORT not found")
      .subflatMap(ps =>
        ps.toIntOption.toRight(s"Expected int in HTTP_PORT env variable, but got $ps"),
      )
      .subflatMap(pi => Port.fromInt(pi).toRight(s"No such port $pi"))
      .leftMap(new IllegalArgumentException(_))
      .rethrowT

  private def getNotionApiKey[F[_]: Env: MonadThrow]: F[String] =
    OptionT(Env[F].get("NOTION_KEY"))
      .toRight("NOTION_API_KEY not found")
      .leftMap(new IllegalArgumentException(_))
      .rethrowT
}
