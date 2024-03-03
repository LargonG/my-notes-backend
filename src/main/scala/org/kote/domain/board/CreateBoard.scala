package org.kote.domain.board

import org.kote.common.tethys.TethysInstances
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateBoard(
    title: String,
    owner: UserId,
)

object CreateBoard extends TethysInstances {
  @nowarn
  implicit val createBoardReader: JsonReader[CreateBoard] = jsonReader

  @nowarn
  implicit val createBoardWriter: JsonWriter[CreateBoard] = jsonWriter

  implicit val createBoardSchema: Schema[CreateBoard] =
    Schema.derived.description("Запрос создания доски")
}
