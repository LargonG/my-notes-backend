package org.kote.domain.user

import org.kote.domain.user.User.{NotionAccessToken, TrelloAccessToken, UserId, UserPassword}

import java.time.Instant
import java.util.UUID

final case class User(
    id: UserId,
    notion: Option[NotionAccessToken],
    trello: Option[TrelloAccessToken],
    name: String,
    password: UserPassword,
    registeredIn: Instant,
) {
  def toUnsafeResponse: UnsafeUserResponse =
    UnsafeUserResponse(id, notion, trello, name, password, registeredIn)

  def toResponse: UserResponse =
    UserResponse(id, name, registeredIn)
}

object User {
  type TrelloAccessToken = String
  type NotionAccessToken = String

  final case class UserId private (inner: UUID) extends AnyVal

  final case class UserPassword(inner: String) extends AnyVal

  final case class AuthToken(token: String) extends AnyVal

  def fromCreateUser(uuid: UUID, date: Instant, createUser: CreateUser): User =
    User(UserId(uuid), None, None, createUser.name, createUser.password, date)
}
