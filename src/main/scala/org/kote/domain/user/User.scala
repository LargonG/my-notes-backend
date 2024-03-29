package org.kote.domain.user

import org.kote.client.notion.NotionUserResponse
import org.kote.common.tethys.TethysInstances
import org.kote.domain.user.User.{UserId, UserPassword}
import sttp.tapir.Schema
import tethys.{JsonReader, JsonWriter}

import java.time.Instant
import java.util.UUID

final case class User(
    id: UserId,
    name: String,
    password: UserPassword,
    registeredIn: Instant,
) {
  def toUnsafeResponse(notionUserResponse: Option[NotionUserResponse] = None): UnsafeUserResponse =
    UnsafeUserResponse(
      id.inner,
      name,
      password.inner,
      registeredIn,
      notionUserResponse.map(_.id),
      notionUserResponse.flatMap(_.name),
    )

  def toResponse: UserResponse =
    UserResponse(id.inner, name, registeredIn)
}

object User {
  def fromCreateUser(uuid: UUID, date: Instant, createUser: CreateUser): User =
    User(UserId(uuid), createUser.name, UserPassword(createUser.password), date)

  final case class UserPassword(inner: String) extends AnyVal

  final case class UserId(inner: UUID) extends AnyVal

  object UserId extends TethysInstances {
    implicit val userIdReader: JsonReader[UserId] = JsonReader[UUID].map(UserId.apply)
    implicit val userIdWriter: JsonWriter[UserId] = JsonWriter[UUID].contramap(_.inner)
    implicit val userIdSchema: Schema[UserId] = Schema.derived.description("ID пользователя")
  }
}
