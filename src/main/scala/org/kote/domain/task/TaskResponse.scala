package org.kote.domain.task

import org.kote.common.tethys.TethysInstances
import org.kote.domain.content.Content
import org.kote.domain.task.Task.{Status, TaskId}
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import java.time.Instant
import scala.annotation.nowarn

final case class TaskResponse(
    id: TaskId,
    title: String,
    assigns: List[UserId],
    status: Status,
    content: Content,
    createdAt: Instant,
    updatedAt: Instant,
)

object TaskResponse extends TethysInstances {
  @nowarn
  implicit val taskResponseReader: JsonReader[TaskResponse] = jsonReader

  @nowarn
  implicit val taskResponseWriter: JsonWriter[TaskResponse] = jsonWriter

  implicit val taskResponseSchema: Schema[TaskResponse] =
    Schema.derived.description("Задача")
}
