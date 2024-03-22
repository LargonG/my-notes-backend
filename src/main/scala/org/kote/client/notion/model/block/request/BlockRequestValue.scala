package org.kote.client.notion.model.block.request

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import org.kote.client.notion.model.block.BlockType
import org.kote.client.notion.model.block.BlockType.ParagraphType
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.utils.Typed

sealed trait BlockRequestValue

object BlockRequestValue {
  implicit val blockRequestValueEncoder: Encoder[BlockRequestValue] =
    Encoder.instance { case paragraph @ ParagraphRequest(_) =>
      paragraph.asJson
    }

  implicit val blockRequestValueType: Typed[BlockRequestValue, BlockType] = {
    case ParagraphRequest(_) => ParagraphType
  }
}

final case class ParagraphRequest(richText: List[RichText]) extends BlockRequestValue

object ParagraphRequest {
  implicit val paragraphRequestEncoder: Encoder[ParagraphRequest] =
    Encoder.instance { source =>
      Json.obj(
        "type" -> "text".asJson,
        "rich_text" -> source.richText.asJson,
      )
    }
}
