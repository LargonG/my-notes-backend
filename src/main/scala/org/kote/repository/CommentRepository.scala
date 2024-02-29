package org.kote.repository

import org.kote.domain.comment.Comment
import org.kote.domain.comment.Comment.CommentId

trait CommentRepository[F[_]] extends Repository[F, Comment, CommentId]
