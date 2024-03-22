package org.kote.client.notion.model.property

import cats.implicits.toFunctorOps
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import org.kote.client.notion.{decodeType, encodeType}

/** Может быть: "rich_text", "select", "status", "title", "people", "files"
  */
sealed trait PropertyType {
  val value: String
}

object PropertyType {
  implicit def asString(me: PropertyType): String = me.value

  implicit val encoder: Encoder[PropertyType] =
    Encoder.instance {
      case RichTextType         => RichTextType.asJson
      case SelectType           => SelectType.asJson
      case StatusType           => StatusType.asJson
      case TitleType            => TitleType.asJson
      case PeopleType           => PeopleType.asJson
      case FilesType            => FilesType.asJson
      case other @ OtherType(_) => other.asJson
    }

  implicit val decoder: Decoder[PropertyType] =
    List[Decoder[PropertyType]](
      Decoder[RichTextType.type].widen,
      Decoder[SelectType.type].widen,
      Decoder[StatusType.type].widen,
      Decoder[TitleType.type].widen,
      Decoder[PeopleType.type].widen,
      Decoder[FilesType.type].widen,
      Decoder[OtherType].widen,
    ).reduceLeft(_ or _)
}

case object RichTextType extends PropertyType {
  override val value = "rich_text"

  implicit val encoder: Encoder[RichTextType.type] =
    encodeType(value)

  implicit val decoder: Decoder[RichTextType.type] =
    decodeType(value, _ => RichTextType)
}

case object SelectType extends PropertyType {
  override val value: String = "select"

  implicit val encoder: Encoder[SelectType.type] =
    encodeType(value)

  implicit val decoder: Decoder[SelectType.type] =
    decodeType(value, _ => SelectType)
}

case object StatusType extends PropertyType {
  override val value: String = "status"

  implicit val encoder: Encoder[StatusType.type] =
    encodeType(value)

  implicit val decoder: Decoder[StatusType.type] =
    decodeType(value, _ => StatusType)
}

case object TitleType extends PropertyType {
  override val value: String = "title"

  implicit val encoder: Encoder[TitleType.type] =
    encodeType(value)

  implicit val decoder: Decoder[TitleType.type] =
    decodeType(value, _ => TitleType)
}

case object PeopleType extends PropertyType {
  override val value: String = "people"

  implicit val encoder: Encoder[PeopleType.type] =
    encodeType(value)

  implicit val decoder: Decoder[PeopleType.type] =
    decodeType(value, _ => PeopleType)
}

case object FilesType extends PropertyType {
  override val value = "files"

  implicit val encoder: Encoder[FilesType.type] =
    encodeType(value)

  implicit val decoder: Decoder[FilesType.type] =
    decodeType(value, _ => FilesType)
}

case class OtherType(override val value: String) extends PropertyType
object OtherType {
  implicit val encoder: Encoder[OtherType] =
    Encoder.encodeString.contramap(_.value)

  implicit val decoder: Decoder[OtherType] =
    Decoder.decodeString.map(OtherType.apply)
}
