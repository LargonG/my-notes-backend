package org.kote.domain.content.file

import cats.Show
import cats.data.Reader
import org.kote.common.parser.Parser
import org.kote.common.tethys.TethysInstances
import org.kote.domain.content.file.File.FileId
import sttp.tapir.Schema
import tethys.{JsonReader, JsonWriter}

import java.util.UUID

final case class File(
    id: FileId,
    data: String,
) {
  def toResponse: FileResponse =
    FileResponse(id, data)
}

object File {
  final case class FileId(inner: UUID) extends AnyVal

  def fromCreateFile(uuid: UUID, createFile: CreateFile): File =
    File(FileId(uuid), createFile.data)

  object FileId extends TethysInstances {
    implicit val fileIdReader: JsonReader[FileId] = JsonReader[UUID].map(FileId.apply)
    implicit val fileIdWriter: JsonWriter[FileId] = JsonWriter[UUID].contramap(_.inner)
    implicit val fileInSchema: Schema[FileId] = Schema.derived.description("ID файла")
  }

  implicit val showFile: Show[File] = _.data

  implicit def fileParser[E]: Parser[E, FileId, File] =
    (input: LazyList[String]) =>
      Reader { id =>
        Right(File(id, input.mkString))
      }
}
