package org.kote.client.notion.model.block.response

import io.circe.Decoder
import org.kote.client.notion.model.block.BlockType.ParagraphType
import org.kote.client.notion.model.block._
import org.kote.client.notion.model.parent.PageParent

case class BlockResponse(
    id: BlockId,
    parent: PageParent,
    archived: Boolean,
    value: BlockResponseValue,
)

object BlockResponse {
  implicit val blockResponseDecoder: Decoder[BlockResponse] =
    Decoder.instance { cur =>
      for {
        id <- cur.get[BlockId]("id")
        parent <- cur.get[PageParent]("parent")
        valueType <- cur.get[BlockType]("type")
        archived <- cur.get[Boolean]("archived")
        value <- valueType match {
          case ParagraphType => cur.get[ParagraphResponse](valueType)
        }
      } yield response.BlockResponse(id, parent, archived, value)
    }
}
