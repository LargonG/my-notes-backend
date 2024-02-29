package org.kote.repository.ram

import cats.Functor
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.comment.Comment
import org.kote.domain.comment.Comment.CommentId
import org.kote.repository.CommentRepository

class RamCommentRepository[F[_]: Functor](cache: Cache[F, CommentId, Comment])
    extends CommentRepository[F] {

  override def create(comment: Comment): F[Long] = cache.add(comment.id, comment).as(1L)

  override def list: F[List[Comment]] = cache.values

  override def get(id: CommentId): OptionT[F, Comment] = OptionT(cache.get(id))

  override def delete(id: CommentId): OptionT[F, Comment] = OptionT(cache.remove(id))
}
