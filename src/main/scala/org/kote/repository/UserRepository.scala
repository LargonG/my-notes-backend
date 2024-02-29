package org.kote.repository

import org.kote.domain.user.User
import org.kote.domain.user.User.{NotionAccessToken, TrelloAccessToken, UserId, UserPassword}
import org.kote.repository.UserRepository.UserUpdateCommand

trait UserRepository[F[_]] extends UpdatableRepository[F, User, UserId, UserUpdateCommand] {}

object UserRepository {
  sealed trait UserUpdateCommand extends UpdateCommand

  final case class UpdateName(name: String) extends UserUpdateCommand
  final case class UpdatePassword(password: UserPassword) extends UserUpdateCommand
  final case class UpdateTrelloAccessToken(token: TrelloAccessToken) extends UserUpdateCommand
  final case class UpdateNotionAccessToken(token: NotionAccessToken) extends UserUpdateCommand
}
