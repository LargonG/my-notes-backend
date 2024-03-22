package org.kote.domain.board

import org.kote.common.tethys.TethysInstances
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class BoardResponse(
    id: BoardId,
    title: String,
    owner: UserId,
)

object BoardResponse extends TethysInstances {
  @nowarn
  implicit val boardResponseReader: JsonReader[BoardResponse] = jsonReader

  @nowarn
  implicit val boardResponseWriter: JsonWriter[BoardResponse] = jsonWriter

  implicit val boardResponseSchema: Schema[BoardResponse] =
    Schema.derived.description("Доска")
}
