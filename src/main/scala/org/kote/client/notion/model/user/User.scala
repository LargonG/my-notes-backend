package org.kote.client.notion.model.user

import java.util.UUID

case class UserResponse(
    id: UserId,
    name: Option[String],
)

case class UserRequest(
    id: UserId,
)

// Это другой UserId, это тот, который в Notion
final case class UserId(inner: UUID) extends AnyVal

object UserId {}
