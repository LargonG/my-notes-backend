package org.kote.domain.user

import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateUser(
    name: String,
    password: String,
    notionUserName: String,
)

object CreateUser {
  @nowarn
  implicit val createUserReader: JsonReader[CreateUser] = jsonReader

  @nowarn
  implicit val createUserWriter: JsonWriter[CreateUser] = jsonWriter

  implicit val createUserSchema: Schema[CreateUser] =
    Schema.derived.description("Запрос создания нового пользователя")
}
