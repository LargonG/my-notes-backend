package org.kote.domain.content.file

import org.kote.common.tethys.TethysInstances
import org.kote.domain.content.file.File.FileId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class FileResponse(
    uuid: FileId,
    data: String,
)

object FileResponse extends TethysInstances {
  @nowarn
  implicit val fileResponseReader: JsonReader[FileResponse] = jsonReader

  @nowarn
  implicit val fileResponseWriter: JsonWriter[FileResponse] = jsonWriter

  implicit val fileResponseSchema: Schema[FileResponse] =
    Schema.derived.description("Файл")
}
