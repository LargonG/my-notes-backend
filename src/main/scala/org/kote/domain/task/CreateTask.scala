package org.kote.domain.task

import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group.GroupId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateTask(
    board: BoardId,
    group: GroupId,
    title: String,
)

object CreateTask {
  @nowarn
  implicit val createTaskReader: JsonReader[CreateTask] = jsonReader

  @nowarn
  implicit val createTaskWriter: JsonWriter[CreateTask] = jsonWriter

  implicit val createTaskSchema: Schema[CreateTask] =
    Schema.derived.description("Запрос добавления задачи в колонку")
}
