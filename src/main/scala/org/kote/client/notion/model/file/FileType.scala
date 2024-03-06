package org.kote.client.notion.model.file

import cats.implicits.toFunctorOps
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import org.kote.client.notion.model.property.PropertyType.{decodeString, encodeString}

sealed trait FileType {
  val value: String
}

object FileType {
  def encodeString[T](value: String): Encoder[T] =
    Encoder.encodeString.contramap(_ => value)

  def decodeString[T](expected: String, constructor: String => T): Decoder[T] =
    Decoder.decodeString.emap(actual =>
      if (actual == expected) Right(constructor(actual))
      else Left("Unsupported file type"),
    )

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
    encodeString(value)

  implicit val decoder: Decoder[NotionFileType.type] =
    decodeString(value, _ => NotionFileType)
}

case object ExternalFileType extends FileType {
  override val value = "external"

  implicit val encoder: Encoder[ExternalFileType.type] =
    encodeString(value)

  implicit val decoder: Decoder[ExternalFileType.type] =
    decodeString(value, _ => ExternalFileType)
}
