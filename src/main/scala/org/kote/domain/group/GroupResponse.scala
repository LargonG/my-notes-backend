package org.kote.domain.group

import org.kote.common.tethys.TethysInstances
import org.kote.domain.group.Group.GroupId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class GroupResponse(
    id: GroupId,
    title: String,
)

object GroupResponse extends TethysInstances {
  @nowarn
  implicit val groupResponseReader: JsonReader[GroupResponse] = jsonReader

  @nowarn
  implicit val groupResponseWriter: JsonWriter[GroupResponse] = jsonWriter

  implicit val groupResponseSchema: Schema[GroupResponse] = Schema.derived.description("Колонка")
}
