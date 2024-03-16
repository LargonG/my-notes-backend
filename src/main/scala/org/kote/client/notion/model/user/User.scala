package org.kote.client.notion.model.user

import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Encoder}

import java.util.UUID

/** Общие данные пользователя
  * @param id
  *   UUID в notion
  * @param name
  *   Имя - видимость зависит от прав клиента
  */
case class UserResponse(
    id: UserId,
    name: Option[String],
)

object UserResponse {
  implicit val userResponseDecoder: Decoder[UserResponse] = deriveDecoder
}

/** Запрос добавления пользователя в какие-то объекты
  * @param id
  *   UUID в notion
  */
case class UserRequest(
    id: UserId,
)

object UserRequest {
  implicit val userRequestEncode: Encoder[UserRequest] =
    Encoder.forProduct2("object", "id")(source => ("user", source.id))
}

final case class UserId(inner: UUID) extends AnyVal {
  override def toString: String = inner.toString
}

object UserId {
  implicit val userIdEncoder: Encoder[UserId] = Encoder.encodeUUID.contramap(_.inner)
  implicit val userIdDecoder: Decoder[UserId] = Decoder.decodeUUID.map(UserId.apply)
}
