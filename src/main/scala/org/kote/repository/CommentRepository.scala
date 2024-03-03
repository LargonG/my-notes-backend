package org.kote.repository

import cats.Functor
import org.kote.common.cache.Cache
import org.kote.domain.comment.Comment
import org.kote.domain.comment.Comment.CommentId
import org.kote.repository.inmemory.InMemoryCommentRepository

trait CommentRepository[F[_]] extends Repository[F, Comment, CommentId]

object CommentRepository {
  def inMemory[F[_]: Functor](cache: Cache[F, CommentId, Comment]): CommentRepository[F] =
    new InMemoryCommentRepository[F](cache)
}
