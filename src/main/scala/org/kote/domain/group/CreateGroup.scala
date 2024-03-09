package org.kote.domain.group

import org.kote.common.tethys.TethysInstances
import org.kote.domain.board.Board.BoardId
import org.kote.domain.task.Task.TaskId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateGroup(
    parent: BoardId,
    title: String,
    tasks: List[TaskId],
)

object CreateGroup extends TethysInstances {
  @nowarn
  implicit val createGroupReader: JsonReader[CreateGroup] = jsonReader

  @nowarn
  implicit val createGroupWriter: JsonWriter[CreateGroup] = jsonWriter

  implicit val createGroupSchema: Schema[CreateGroup] =
    Schema.derived.description("Запрос создания колонки")
}
