package org.kote.client.notion

import cats.data.OptionT
import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.comment.{CommentRequest, CommentResponse}
import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.list.PaginatedList.Cursor
import org.kote.client.notion.model.page.PageId
import sttp.client3.{SttpBackend, UriContext}
import sttp.client3.circe._

trait NotionCommentClient[F[_]] {
  def create(request: CommentRequest): OptionT[F, CommentResponse]

  def get(page: PageId): OptionT[F, List[CommentResponse]]
}

final class NotionCommentHttpClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    implicit val config: NotionConfiguration,
) extends NotionCommentClient[F] {
  private val comments = s"${config.url}/$v1/comments"

  override def create(request: CommentRequest): OptionT[F, CommentResponse] =
    OptionT(
      basicRequestWithHeaders
        .post(uri"$comments")
        .body(request)
        .response(unwrap[F, CommentResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def get(page: PageId): OptionT[F, List[CommentResponse]] = {
    def tick(cursor: Option[Cursor]): OptionT[F, PaginatedList[CommentResponse]] =
      OptionT(
        basicRequestWithHeaders
          .get(uri"$comments?block_id=$page&$cursor")
          .response(unwrap[F, PaginatedList[CommentResponse]])
          .readTimeout(config.timeout)
          .send(sttpBackend)
          .flatMap(optionIfSuccess(_)),
      )

    concatPaginatedLists(tick)
  }
}
