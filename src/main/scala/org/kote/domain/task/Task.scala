package org.kote.domain.task

import org.kote.common.tethys.TethysInstances
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.content.Content
import org.kote.domain.task.Task.{Status, TaskId}
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.{JsonReader, JsonWriter}

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
    TaskResponse(
      id,
      title,
      assigns,
      status,
      content.text,
      content.files,
      comments,
      createdAt,
      updatedAt,
    )
}

object Task {
  type Status = String

  def fromCreateTask(id: UUID, date: Instant, createTask: CreateTask): Task =
    Task(
      id = TaskId(id),
      title = createTask.title,
      assigns = List(),
      status = "", // TODO: тут должно быть что-то поумнее
      content = Content("", List()),
      comments = List(),
      createdAt = date,
      updatedAt = date,
    )

  final case class TaskId(inner: UUID) extends AnyVal

  object TaskId extends TethysInstances {
    implicit val taskIdReader: JsonReader[TaskId] = JsonReader[UUID].map(TaskId.apply)
    implicit val taskIdWriter: JsonWriter[TaskId] = JsonWriter[UUID].contramap(_.inner)
    implicit val taskIdSchema: Schema[TaskId] = Schema.derived.description("ID задачи")
  }
}
