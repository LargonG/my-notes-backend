package org.kote.client.notion.model.file

import cats.implicits.toFunctorOps
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax.EncoderOps
import org.kote.client.notion.model.file.FileHeader.{urlDecoder, urlEncoder}

import java.net.URL
import java.time.Instant

final case class FileHeader(name: String, file: File)

object FileHeader {
  implicit val urlEncoder: Encoder[URL] = Encoder.encodeURI.contramap(_.toURI)
  implicit val urlDecoder: Decoder[URL] = Decoder.decodeURI.map(_.toURL)

  implicit val fileHeaderEncoder: Encoder[FileHeader] = {
    def chooseName(file: File): String = file match {
      case ExternalFile(_)  => ExternalFileType
      case NotionFile(_, _) => NotionFileType
    }

    Encoder.instance { source =>
      Json.obj(
        "name" -> source.name.asJson,
        chooseName(source.file) -> source.file.asJson,
      )
    }
  }

  implicit val fileHeaderDecoder: Decoder[FileHeader] =
    Decoder.instance { cursor =>
      for {
        name <- cursor.get[String]("name")
        fileType <- cursor.get[FileType]("type")
        file <- fileType match {
          case NotionFileType   => cursor.get[NotionFile](fileType)
          case ExternalFileType => cursor.get[ExternalFile](fileType)
        }
      } yield FileHeader(name, file)
    }
}

sealed trait File

object File {
  implicit val fileEncoder: Encoder[File] = Encoder.instance {
    case external @ ExternalFile(_)    => external.asJson
    case notionFile @ NotionFile(_, _) => notionFile.asJson
  }

  implicit val fileDecoder: Decoder[File] =
    List[Decoder[File]](
      Decoder[ExternalFile].widen,
      Decoder[NotionFile].widen,
    ).reduceLeft(_ or _)
}

final case class ExternalFile(url: URL) extends File

object ExternalFile {
  implicit val externalFileEncoder: Encoder[ExternalFile] = deriveEncoder
  implicit val externalFileDecoder: Decoder[ExternalFile] = deriveDecoder
}

final case class NotionFile(url: URL, expiryTime: Instant) extends File

object NotionFile {
  implicit val notionFileEncoder: Encoder[NotionFile] =
    Encoder.forProduct2("url", "expiry_time")(source => (source.url, source.expiryTime))

  implicit val notionFileDecoder: Decoder[NotionFile] =
    Decoder.forProduct2("url", "expiry_time")(NotionFile.apply)
}
