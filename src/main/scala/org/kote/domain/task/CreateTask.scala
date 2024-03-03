package org.kote.domain.task

import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateTask(
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
