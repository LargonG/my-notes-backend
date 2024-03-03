package org.kote.domain.content.file

import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateFile(
    data: String,
)

object CreateFile {
  @nowarn
  implicit val createFileReader: JsonReader[CreateFile] = jsonReader

  @nowarn
  implicit val createFileWriter: JsonWriter[CreateFile] = jsonWriter

  implicit val createFileSchema: Schema[CreateFile] =
    Schema.derived.description("Запрос создания файла")
}
