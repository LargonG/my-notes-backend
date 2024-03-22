package org.kote.domain.content

import org.kote.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class Content(
    text: String,
)

object Content extends TethysInstances {
  @nowarn
  implicit val tethysContentReader: JsonReader[Content] = jsonReader

  @nowarn
  implicit val tethysContentWriter: JsonWriter[Content] = jsonWriter

  implicit val contentSchema: Schema[Content] = Schema.derived
}
