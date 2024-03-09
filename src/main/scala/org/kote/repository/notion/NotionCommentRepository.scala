package org.kote.repository.notion

import cats.Monad
import cats.data.OptionT
import cats.implicits.catsSyntaxApplicativeId
import cats.syntax.functor._
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapter, ToAdapter}
import org.kote.client.notion._
import org.kote.domain.comment.Comment
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.task.Task
import org.kote.repository.CommentRepository

case class NotionCommentRepository[F[_]: Monad](client: NotionCommentClient[F])(implicit
    val commentAdapter: Adapter[Comment, NotionCommentCreateRequest, NotionCommentResponse],
    val idAdapter: Adapter[CommentId, NotionPageId, NotionCommentId],
) extends CommentRepository[F] {
  override def create(obj: Comment): F[Long] = (for {
    response <- client.create(obj.toRequest)
  } yield response.fromResponse).as(1L).getOrElse(0L)

  override def list(taskId: Task.TaskId): OptionT[F, List[Comment]] = ???

  override def get(id: CommentId): OptionT[F, Comment] = (for {
                                                                          list <- client.get(id.toRequest)
      response = list.find(r => r.id.fromResponse == id)
  } yield response
    .map(_.fromResponse)).flatTransform(_.flatten.pure)

  override def delete(id: CommentId): OptionT[F, Comment] = get(id)

}
