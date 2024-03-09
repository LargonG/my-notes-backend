package org.kote.repository

import cats.data.OptionT
import cats.{Functor, Monad}
import org.kote.adapter.Adapter
import org.kote.client.notion._
import org.kote.common.cache.Cache
import org.kote.domain.comment.Comment
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.task.Task.TaskId
import org.kote.repository.inmemory.InMemoryCommentRepository
import org.kote.repository.notion.NotionCommentRepository

trait CommentRepository[F[_]] extends Repository[F, Comment, CommentId] {
  def list(taskId: TaskId): OptionT[F, List[Comment]]
}

object CommentRepository {
  def inMemory[F[_]: Functor](cache: Cache[F, CommentId, Comment]): CommentRepository[F] =
    new InMemoryCommentRepository[F](cache)

  def notion[F[_]: Monad](client: NotionCommentClient[F])(implicit
      commentAdapter: Adapter[Comment, NotionCommentCreateRequest, NotionCommentResponse],
      idAdapter: Adapter[CommentId, NotionPageId, NotionCommentId],
  ) = new NotionCommentRepository[F](client)
}
