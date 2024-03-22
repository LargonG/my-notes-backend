package org.kote.domain.comment

import org.kote.common.tethys.TethysInstances
import org.kote.domain.content.Content
import org.kote.domain.task.Task.TaskId
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateComment(
    parent: TaskId,
    author: UserId,
    content: Content,
)

object CreateComment extends TethysInstances {
  @nowarn
  implicit val createCommentReader: JsonReader[CreateComment] = jsonReader

  @nowarn
  implicit val createCommentWriter: JsonWriter[CreateComment] = jsonWriter

  implicit val createCommentSchema: Schema[CreateComment] =
    Schema.derived.description("Запрос создания комментария")
}
