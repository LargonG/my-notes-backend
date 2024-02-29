package org.kote.domain.comment

import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.content.Content
import org.kote.domain.user.User.UserId

import java.time.Instant
import java.util.UUID

final case class Comment(
    id: CommentId,
    author: UserId,
    content: Content,
    createdAt: Instant,
)

object Comment {
  final case class CommentId(inner: UUID) extends AnyVal
}
