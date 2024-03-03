package org.kote

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.std.Env
import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{Host, Port}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
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
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import java.nio.charset.StandardCharsets

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      userCache <- Cache.ram[IO, UserId, User]
      taskCache <- Cache.ram[IO, TaskId, Task]
      groupCache <- Cache.ram[IO, GroupId, Group]
      boardCache <- Cache.ram[IO, BoardId, Board]
      commentCache <- Cache.ram[IO, CommentId, Comment]
      fileCache <- Cache.disk[IO, FileId, File]("files", StandardCharsets.UTF_8)
      endpoints <- IO.delay {
        val userRepo = UserRepository.inMemory(userCache)
        val taskRepo = TaskRepository.inMemory(taskCache)
        val groupRepo = GroupRepository.inMemory(groupCache)
        val boardRepo = BoardRepository.inMemory(boardCache)
        val commentRepo = CommentRepository.inMemory(commentCache)
        val fileRepo = FileRepository.inMemory(fileCache)

        List(
          UserController.make(UserService.fromRepository(userRepo)),
          TaskController.make(TaskService.fromRepository(boardRepo, groupRepo, taskRepo)),
          GroupController.make(GroupService.fromRepository(groupRepo)),
          BoardController.make(BoardService.fromRepository(boardRepo)),
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
}
