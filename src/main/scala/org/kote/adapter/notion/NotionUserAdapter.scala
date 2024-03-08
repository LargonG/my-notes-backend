package org.kote.adapter.notion

import org.kote.adapter.Adapter
import org.kote.client.notion.model.user.{UserRequest, UserResponse}
import org.kote.domain.user.User

final case class NotionUserAdapter() extends Adapter[User, UserRequest, UserResponse] {
  override def toRequest(value: User): UserRequest = ???

  override def fromResponse(response: UserResponse): User = ???
}
