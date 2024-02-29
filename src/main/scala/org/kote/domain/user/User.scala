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
)

object User {
  type TrelloAccessToken = String
  type NotionAccessToken = String

  final case class UserId(inner: UUID) extends AnyVal

  final case class UserPassword(inner: String) extends AnyVal
}
