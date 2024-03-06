package org.kote.client.notion.model.block

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import org.kote.client.notion.model.parent.PageParent
import org.kote.client.notion.model.text.RichText

import java.util.UUID

case class BlockResponse(
    id: BlockId,
    parent: PageParent,
    valueType: String,
    achieved: Boolean,
    value: BlockResponseValue,
)

sealed trait BlockResponseValue

final case class ParagraphResponse(richText: List[RichText]) extends BlockResponseValue

case class BlockRequest(
    value: BlockRequestValue,
)

object BlockRequest {
  implicit val blockRequestEncoder: Encoder[BlockRequest] = {
    def chooseType(value: BlockRequestValue): String = value match {
      case ParagraphRequest(_) => "paragraph"
    }

    Encoder.instance { source =>
      Json.obj(
        "object" -> "block".asJson,
        "type" -> chooseType(source.value).asJson,
        chooseType(source.value) -> source.value.asJson,
      )
    }
  }
}

sealed trait BlockRequestValue

object BlockRequestValue {
  implicit val blockRequestValueEncoder: Encoder[BlockRequestValue] =
    Encoder.instance { case paragraph @ ParagraphRequest(_) =>
      paragraph.asJson
    }
}

final case class ParagraphRequest(richText: List[RichText]) extends BlockRequestValue

object ParagraphRequest {
  implicit val paragraphRequestEncoder: Encoder[ParagraphRequest] =
    Encoder.instance { source =>
      Json.obj("type" -> "text".asJson, "rich_text" -> source.richText.asJson)
    }
}

final case class BlockId(inner: UUID) extends AnyVal
