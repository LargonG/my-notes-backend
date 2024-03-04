package org.kote.client.notion

import cats.data.OptionT
import org.kote.client.notion.model.user.{UserId, UserResponse}

trait NotionUserClient[F[_]] {
  def list: F[List[UserResponse]]

  def get(id: UserId): OptionT[F, UserResponse]

  def me: OptionT[F, UserResponse]
}
