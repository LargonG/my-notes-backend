package org.kote.client.notion.model.block

import cats.implicits.toFunctorOps
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import org.kote.client.notion.{decodeType, encodeType}

sealed trait BlockType {
  val value: String
}

object BlockType {
  implicit def asString(me: BlockType): String = me.value

  implicit val encoder: Encoder[BlockType] =
    Encoder.instance { case ParagraphType =>
      ParagraphType.asJson
    }

  implicit val decoder: Decoder[BlockType] =
    List[Decoder[BlockType]](
      Decoder[ParagraphType.type].widen,
    ).reduceLeft(_ or _)
}

case object ParagraphType extends BlockType {
  override val value = "paragraph"
  implicit val encoder: Encoder[ParagraphType.type] =
    encodeType(value)

  implicit val decoder: Decoder[ParagraphType.type] =
    decodeType(value, _ => ParagraphType)
}
