package org.kote.client.notion.model.block.request

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import org.kote.client.notion.utils.Typed.ToTyped
import org.kote.client.notion.model.block.BlockType.asString
import org.kote.client.notion.model.text.RichText

case class BlockRequest(
    value: BlockRequestValue,
)

object BlockRequest {
  implicit val blockRequestEncoder: Encoder[BlockRequest] =
    Encoder.instance { source =>
      Json.obj(
        "object" -> "block".asJson,
        "type" -> source.value.toType.asJson,
        asString(source.value.toType) -> source.value.asJson,
      )
    }

  def paragraph(list: List[RichText]): BlockRequest =
    BlockRequest(ParagraphRequest(list))
}
