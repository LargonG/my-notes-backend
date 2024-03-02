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
) {
  def toResponse: TaskResponse =
    TaskResponse(id, title, assigns, status, content, comments, createdAt, updatedAt)
}

object Task {
  type Status = String
  final case class TaskId private (inner: UUID) extends AnyVal

  def fromCreateTask(id: UUID, date: Instant, createTask: CreateTask): Task =
    Task(
      id = TaskId(id),
      title = createTask.title,
      assigns = List(),
      status = "", // TODO: тут должно быть что-то поумнее
      content = Content(List(), List()),
      comments = List(),
      createdAt = date,
      updatedAt = date,
    )
}
