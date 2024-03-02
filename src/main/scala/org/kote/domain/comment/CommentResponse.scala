package org.kote.domain.comment

import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.content.Content
import org.kote.domain.user.User.UserId

import java.time.Instant

final case class CommentResponse(
    id: CommentId,
    author: UserId,
    content: Content,
    createdAt: Instant,
)
