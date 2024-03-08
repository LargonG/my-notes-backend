package org.kote.repository.notion

import cats.Monad
import cats.data.OptionT
import cats.implicits.catsSyntaxApplicativeId
import cats.syntax.functor._
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapter, ToAdapter}
import org.kote.client.notion
import org.kote.client.notion.NotionCommentClient
import org.kote.domain.comment.Comment
import org.kote.domain.comment.Comment.CommentId
import org.kote.repository.CommentRepository
import org.kote.repository.notion.NotionCommentRepository._

case class NotionCommentRepository[F[_]: Monad](client: NotionCommentClient[F])(implicit
    val commentAdapter: Adapter[Comment, NotionCommentRequest, NotionCommentResponse],
    val idAdapter: Adapter[CommentId, NotionPageId, NotionCommentId],
) extends CommentRepository[F] {
  override def create(obj: Comment): F[Long] = (for {
    response <- client.create(obj.toRequest)
  } yield response.fromResponse).as(1L).getOrElse(0L)

  // нет
  override def list: F[List[Comment]] = ???

  override def get(id: CommentId): OptionT[F, Comment] = (for {
    list <- client.get(id.toRequest)
    response = list.find(r => r.id.fromResponse == id)
  } yield response
    .map(_.fromResponse)).flatTransform(_.flatten.pure)

  override def delete(id: CommentId): OptionT[F, Comment] = get(id)
}

object NotionCommentRepository {
  private type NotionPageId = notion.model.page.PageId
  private type NotionCommentId = notion.model.comment.CommentId
  private type NotionCommentRequest = notion.model.comment.CommentRequest
  private type NotionCommentResponse = notion.model.comment.CommentResponse
}
