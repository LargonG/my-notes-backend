package org.kote.client.notion.model.page

import org.kote.client.notion.model.block.response.BlockResponse

/** Объединяет поля properties у page и children blocks
  *
  * @param info
  *   properties
  * @param blocks
  *   children
  */
final case class NotionPageFullResponse(
    info: PageResponse,
    blocks: List[BlockResponse],
)
