package org.kote.client.notion.model.block

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

sealed trait BlockRequestValue

final case class ParagraphRequest(richText: List[RichText]) extends BlockRequestValue

final case class BlockId(inner: UUID) extends AnyVal
