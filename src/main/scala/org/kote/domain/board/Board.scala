package org.kote.domain.board

import org.kote.common.tethys.TethysInstances
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.{JsonReader, JsonWriter}

import java.util.UUID

final case class Board(
    id: BoardId,
    title: String,
    owner: UserId,
) {
  def toResponse: BoardResponse =
    BoardResponse(id, title, owner)
}

object Board {
  final case class BoardId(inner: UUID) extends AnyVal

  object BoardId extends TethysInstances {
    implicit val boardIdReader: JsonReader[BoardId] = JsonReader[UUID].map(BoardId.apply)
    implicit val boardIdWriter: JsonWriter[BoardId] = JsonWriter[UUID].contramap(_.inner)
    implicit val boardIdSchema: Schema[BoardId] = Schema.derived.description("ID таблицы")
  }

  def fromCreateBoard(uuid: UUID, createBoard: CreateBoard): Board =
    Board(BoardId(uuid), createBoard.title, createBoard.createdBy)
}
