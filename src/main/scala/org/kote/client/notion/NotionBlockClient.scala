package org.kote.client.notion

import cats.data.OptionT
import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.block.{BlockId, BlockRequest, BlockResponse}
import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.list.PaginatedList.Cursor
import org.kote.client.notion.model.page.PageId
import org.kote.client.notion.utils.Query.ToQuery
import sttp.client3.circe._
import sttp.client3.{SttpBackend, UriContext}

trait NotionBlockClient[F[_]] {
  def append(pageId: PageId, children: List[BlockRequest]): OptionT[F, PaginatedList[BlockResponse]]

  def get(id: BlockId): OptionT[F, BlockResponse]

  def getContent(id: PageId): OptionT[F, List[BlockResponse]]

  // todo: не понятно, какой тут нужен тип, разобраться
  def update(id: BlockId, request: BlockRequest): OptionT[F, BlockResponse]

  def delete(id: BlockId): OptionT[F, BlockResponse]
}

final class NotionBlockHttpClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    implicit val config: NotionConfiguration,
) extends NotionBlockClient[F] {
  private val blocks = s"${config.url}/$v1/blocks"

  override def append(
      pageId: PageId,
      children: List[BlockRequest],
  ): OptionT[F, PaginatedList[BlockResponse]] =
    OptionT(
      basicRequestWithHeaders
        .patch(uri"$blocks/$pageId/children")
        .body(children)
        .response(unwrap[F, PaginatedList[BlockResponse]])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def get(id: BlockId): OptionT[F, BlockResponse] =
    OptionT(
      basicRequestWithHeaders
        .get(uri"$blocks/$id")
        .response(unwrap[F, BlockResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def getContent(id: PageId): OptionT[F, List[BlockResponse]] = {
    def tick(cursor: Option[Cursor]): OptionT[F, PaginatedList[BlockResponse]] =
      OptionT(
        basicRequestWithHeaders
          .get(uri"$blocks/$id/children?${cursor.map(_.toQuery)}")
          .response(unwrap[F, PaginatedList[BlockResponse]])
          .readTimeout(config.timeout)
          .send(sttpBackend)
          .flatMap(optionIfSuccess(_)),
      )

    concatPaginatedLists(tick)
  }

  override def update(id: BlockId, request: BlockRequest): OptionT[F, BlockResponse] =
    OptionT(
      basicRequestWithHeaders
        .patch(uri"$blocks/$id")
        .response(unwrap[F, BlockResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def delete(id: BlockId): OptionT[F, BlockResponse] =
    OptionT(
      basicRequestWithHeaders
        .delete(uri"$blocks/$id")
        .response(unwrap[F, BlockResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )
}
