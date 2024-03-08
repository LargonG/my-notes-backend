package org.kote.domain.user

import org.kote.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import java.time.Instant
import java.util.UUID
import scala.annotation.nowarn

final case class UserResponse(
    id: UUID,
    name: String,
    registeredIn: Instant,
)

object UserResponse extends TethysInstances {
  @nowarn
  implicit val userResponseReader: JsonReader[UserResponse] = jsonReader

  @nowarn
  implicit val userResponseWriter: JsonWriter[UserResponse] = jsonWriter

  implicit val userResponseSchema: Schema[UserResponse] =
    Schema.derived.description("Общие данные о пользователе")
}

/** Небезопасный класс, его не следует возвращать любому пользователю, иначе может произойти утечка
  * данных
  * @param id
  *   пользователя
  * @param name
  *   логин
  * @param password
  *   пароль !!! небезопасно
  * @param registeredIn
  *   когда зарегистрирован
  */
final case class UnsafeUserResponse(
    id: UUID,
    name: String,
    password: String,
    registeredIn: Instant,
)

object UnsafeUserResponse extends TethysInstances {
  @nowarn
  implicit val unsafeUserResponseReader: JsonReader[UnsafeUserResponse] = jsonReader

  @nowarn
  implicit val unsafeUserResponseWriter: JsonWriter[UnsafeUserResponse] = jsonWriter

  implicit val unsafeUserResponseSchema: Schema[UnsafeUserResponse] =
    Schema.derived.description("Подробные данные о пользователе")
}
