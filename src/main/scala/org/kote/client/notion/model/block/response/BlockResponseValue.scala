package org.kote.client.notion.model.block.response

import cats.implicits.toFunctorOps
import io.circe.Decoder
import org.kote.client.notion.model.block.BlockType
import org.kote.client.notion.model.block.BlockType.ParagraphType
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.utils.Typed

sealed trait BlockResponseValue

object BlockResponseValue {
  implicit val blockResponseValueDecoder: Decoder[BlockResponseValue] =
    List[Decoder[BlockResponseValue]](
      Decoder[ParagraphResponse].widen,
    ).reduceLeft(_ or _)

  implicit val blockResponseValueType: Typed[BlockResponseValue, BlockType] = {
    case ParagraphResponse(_) => ParagraphType
  }
}

final case class ParagraphResponse(richText: List[RichText]) extends BlockResponseValue

object ParagraphResponse {
  implicit val paragraphResponseDecoder: Decoder[ParagraphResponse] =
    Decoder.instance(_.get[List[RichText]]("rich_text")).map(ParagraphResponse(_))
}
