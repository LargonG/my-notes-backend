package org.kote.domain.comment

import org.kote.common.tethys.TethysInstances
import org.kote.domain.content.file.File.FileId
import org.kote.domain.task.Task.TaskId
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateComment(
    parent: TaskId,
    author: UserId,
    text: String,
    files: List[FileId],
)

object CreateComment extends TethysInstances {
  @nowarn
  implicit val createCommentReader: JsonReader[CreateComment] = jsonReader

  @nowarn
  implicit val createCommentWriter: JsonWriter[CreateComment] = jsonWriter

  implicit val createCommentSchema: Schema[CreateComment] =
    Schema.derived.description("Запрос создания комментария")
}
