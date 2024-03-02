package org.kote.domain.user

import org.kote.domain.user.User.{NotionAccessToken, TrelloAccessToken, UserId, UserPassword}

import java.time.Instant

final case class UserResponse(
    id: UserId,
    name: String,
    registeredIn: Instant,
)

/** Небезопасный класс, его не следует возвращать любому пользователю, иначе может произойти утечка
  * данных
  * @param id
  *   пользователя
  * @param notion
  *   access token !!! небезопасно
  * @param trello
  *   access token !!! небезопасно
  * @param name
  *   логин
  * @param password
  *   пароль !!! небезопасно
  * @param registeredIn
  *   когда зарегистрирован
  */
final case class UnsafeUserResponse(
    id: UserId,
    notion: Option[NotionAccessToken],
    trello: Option[TrelloAccessToken],
    name: String,
    password: UserPassword,
    registeredIn: Instant,
)
