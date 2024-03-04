package org.kote.client.notion

import cats.data.OptionT
import org.kote.client.notion.model.block.{BlockId, BlockRequest, BlockResponse}
import org.kote.client.notion.model.page.PageId

trait NotionBlockClient[F[_]] {
  def append(id: PageId, children: List[BlockRequest]): F[BlockResponse]

  def get(id: BlockId): OptionT[F, BlockResponse]

  def getContent(id: PageId): OptionT[F, List[BlockResponse]]

  // todo: не понятно, какой тут нужен тип, разобраться
  def update(id: BlockId, request: BlockRequest): OptionT[F, BlockResponse]

  def delete(id: BlockId): OptionT[F, BlockResponse]
}
