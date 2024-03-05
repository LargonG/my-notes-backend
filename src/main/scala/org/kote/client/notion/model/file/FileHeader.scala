package org.kote.client.notion.model.file

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.kote.client.notion.model.file.FileHeader.urlDecoder

import java.net.URL
import java.time.Instant

final case class FileHeader(file: File)

object FileHeader {
  implicit val urlDecoder: Decoder[URL] = Decoder.decodeURI.map(_.toURL)

  implicit val fileHeaderDecoder: Decoder[FileHeader] =
    Decoder.instance { cursor =>
      for {
        fileType <- cursor.get[String]("type")
        file <- fileType match {
          case "file"     => cursor.get[NotionFile](fileType)
          case "external" => cursor.get[ExternalFile](fileType)
          case _          => cursor.get[UnsupportedFile.type](fileType)
        }
      } yield FileHeader(file)
    }
}

sealed trait File

case object UnsupportedFile extends File {
  implicit val unsupportedFileDecoder: Decoder[UnsupportedFile.type] = deriveDecoder
}

final case class ExternalFile(url: URL) extends File

object ExternalFile {
  implicit val externalFileDecoder: Decoder[ExternalFile] = deriveDecoder
}

final case class NotionFile(url: URL, expiryTime: Instant) extends File

object NotionFile {
  implicit val notionFileEncoder: Decoder[NotionFile] =
    Decoder.forProduct2("url", "expiry_time")(NotionFile.apply)
}
