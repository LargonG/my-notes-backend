package org.kote.client.notion.model.property

import cats.implicits.toFunctorOps
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import org.kote.client.notion.model.property.PropertyType.{decodeString, encodeString}

/** Может быть: "rich_text", "select", "status", "title", "people", "files"
  */
sealed trait PropertyType {
  val value: String
}

object PropertyType {
  def encodeString[T](value: String): Encoder[T] =
    Encoder.encodeString.contramap(_ => value)

  def decodeString[T](expected: String, constructor: String => T): Decoder[T] =
    Decoder.decodeString.emap(actual =>
      if (actual == expected) Right(constructor(actual))
      else Left("Unsupported property type"),
    )

  implicit def asString(me: PropertyType): String = me.value

  implicit val encoder: Encoder[PropertyType] =
    Encoder.instance {
      case RichTextType => RichTextType.asJson
      case SelectType   => SelectType.asJson
      case StatusType   => StatusType.asJson
      case TitleType    => TitleType.asJson
      case PeopleType   => PeopleType.asJson
      case FilesType    => FilesType.asJson
    }

  implicit val decoder: Decoder[PropertyType] =
    List[Decoder[PropertyType]](
      Decoder[RichTextType.type].widen,
      Decoder[SelectType.type].widen,
      Decoder[TitleType.type].widen,
      Decoder[PeopleType.type].widen,
      Decoder[FilesType.type].widen,
    ).reduceLeft(_ or _)
}

case object RichTextType extends PropertyType {
  override val value = "rich_text"

  implicit val encoder: Encoder[RichTextType.type] =
    encodeString(value)

  implicit val decoder: Decoder[RichTextType.type] =
    decodeString(value, _ => RichTextType)
}

case object SelectType extends PropertyType {
  override val value: String = "select"

  implicit val encoder: Encoder[SelectType.type] =
    encodeString(value)

  implicit val decoder: Decoder[SelectType.type] =
    decodeString(value, _ => SelectType)
}

case object StatusType extends PropertyType {
  override val value: String = "status"

  implicit val encoder: Encoder[StatusType.type] =
    encodeString(value)

  implicit val decoder: Decoder[StatusType.type] =
    decodeString(value, _ => StatusType)
}

case object TitleType extends PropertyType {
  override val value: String = "title"

  implicit val encoder: Encoder[TitleType.type] =
    encodeString(value)

  implicit val decoder: Decoder[TitleType.type] =
    decodeString(value, _ => TitleType)
}

case object PeopleType extends PropertyType {
  override val value: String = "people"

  implicit val encoder: Encoder[PeopleType.type] =
    encodeString(value)

  implicit val decoder: Decoder[PeopleType.type] =
    decodeString(value, _ => PeopleType)
}

case object FilesType extends PropertyType {
  override val value = "files"

  implicit val encoder: Encoder[FilesType.type] =
    encodeString(value)

  implicit val decoder: Decoder[FilesType.type] =
    decodeString(value, _ => FilesType)
}
