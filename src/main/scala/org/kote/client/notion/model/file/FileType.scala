package org.kote.client.notion.model.file

import cats.implicits.toFunctorOps
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import org.kote.client.notion.{decodeType, encodeType}

sealed trait FileType {
  val value: String
}

object FileType {
  implicit def asString(me: FileType): String = me.value

  implicit val encoder: Encoder[FileType] =
    Encoder.instance {
      case NotionFileType   => NotionFileType.asJson
      case ExternalFileType => ExternalFileType.asJson
    }

  implicit val decoder: Decoder[FileType] =
    List[Decoder[FileType]](
      Decoder[NotionFileType.type].widen,
      Decoder[ExternalFileType.type].widen,
    ).reduceLeft(_ or _)
}

case object NotionFileType extends FileType {
  override val value = "file"

  implicit val encoder: Encoder[NotionFileType.type] =
    encodeType(value)

  implicit val decoder: Decoder[NotionFileType.type] =
    decodeType(value, _ => NotionFileType)
}

case object ExternalFileType extends FileType {
  override val value = "external"

  implicit val encoder: Encoder[ExternalFileType.type] =
    encodeType(value)

  implicit val decoder: Decoder[ExternalFileType.type] =
    decodeType(value, _ => ExternalFileType)
}
