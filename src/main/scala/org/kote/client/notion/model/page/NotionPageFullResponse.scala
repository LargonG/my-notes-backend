package org.kote.client.notion.model.page

import org.kote.client.notion.model.block.BlockResponse

final case class NotionPageFullResponse(
    info: PageResponse,
    blocks: List[BlockResponse],
)
