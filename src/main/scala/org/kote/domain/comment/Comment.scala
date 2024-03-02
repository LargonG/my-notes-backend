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
) {
  def toResponse: CommentResponse =
    CommentResponse(id, author, content, createdAt)
}

object Comment {
  final case class CommentId private (inner: UUID) extends AnyVal

  def fromCreateComment(uuid: UUID, date: Instant, createComment: CreateComment): Comment =
    Comment(CommentId(uuid), createComment.author, createComment.content, date)
}
