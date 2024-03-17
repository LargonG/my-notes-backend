package org.kote.controller

import org.kote.common.controller.Controller
import org.kote.domain.board.Board.BoardId
import org.kote.domain.board.{BoardResponse, CreateBoard}
import org.kote.domain.user.User.UserId
import org.kote.service.BoardService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint

class BoardController[F[_]](boardService: BoardService[F]) extends Controller[F] {
  private val standardPath: EndpointInput[Unit] = "api" / "v1" / "board"
  private val pathWithBoardId: EndpointInput[BoardId] = standardPath / path[BoardId]("boardId")

  private val createBoard: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать доску")
      .in(standardPath)
      .in(jsonBody[CreateBoard])
      .out(jsonBody[Option[BoardResponse]])
      .serverLogicSuccess(boardService.create(_).value)

  private val listBoardsByOwner: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список досок пользователя")
      .in(standardPath / query[UserId]("user_id"))
      .out(jsonBody[List[BoardResponse]])
      .serverLogicSuccess(boardService.list)

  private val getBoard: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить доску")
      .in(pathWithBoardId)
      .out(jsonBody[Option[BoardResponse]])
      .serverLogicSuccess(boardService.get(_).value)

  private val deleteBoard: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить доску")
      .in(pathWithBoardId)
      .out(jsonBody[Option[BoardResponse]])
      .serverLogicSuccess(boardService.delete(_).value)

  private val importBoard: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Импортировать таблицы пользователя из сторонних сервисов")
      .in(standardPath / "import" / query[UserId]("user_id"))
      .out(jsonBody[Option[List[BoardResponse]]])
      .serverLogicSuccess(boardService.importFromIntegration)

  private val exportBoard: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Экспортировать таблицу в сторонние сервисы")
      .in(pathWithBoardId / "export")
      .out(jsonBody[Option[BoardResponse]])
      .serverLogicSuccess(boardService.exportToIntegration)

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(createBoard, listBoardsByOwner, getBoard, deleteBoard, importBoard, exportBoard).map(
      _.withTag("Board"),
    )
}

object BoardController {
  def make[F[_]](boardService: BoardService[F]): BoardController[F] =
    new BoardController[F](boardService)
}
