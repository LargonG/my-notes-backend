package org.kote.domain.task

import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.content.Content
import org.kote.domain.task.Task.{Status, TaskId}
import org.kote.domain.user.User.UserId

import java.time.Instant
import java.util.UUID

final case class Task(
    id: TaskId,
    title: String,
    assigns: List[UserId],
    status: Status,
    content: Content,
    comments: List[CommentId],
    createdAt: Instant,
    updatedAt: Instant,
)

object Task {
  type Status = String
  final case class TaskId(inner: UUID) extends AnyVal
}
