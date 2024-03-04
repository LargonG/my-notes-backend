package org.kote.client.notion

import cats.data.OptionT
import org.kote.client.notion.model.comment.{CommentRequest, CommentResponse}
import org.kote.client.notion.model.page.PageId

trait NotionCommentClient[F[_]] {
  def create(request: CommentRequest): F[CommentResponse]

  def get(page: PageId): OptionT[F, List[CommentResponse]]
}
