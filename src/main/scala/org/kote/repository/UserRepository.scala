package org.kote.repository

import cats.{Functor, Monad}
import org.kote.adapter.Adapter
import org.kote.client.notion.{
  NotionUserClient,
  NotionUserId,
  NotionUserRequest,
  NotionUserResponse,
}
import org.kote.common.cache.Cache
import org.kote.domain.user.User
import org.kote.domain.user.User.{UserId, UserPassword}
import org.kote.repository.UserRepository.UserUpdateCommand
import org.kote.repository.inmemory.InMemoryUserRepository
import org.kote.repository.notion.NotionUserRepository

trait UserRepository[F[_]] extends UpdatableRepository[F, User, UserId, UserUpdateCommand] {
  def all: F[List[User]]
}

object UserRepository {
  sealed trait UserUpdateCommand extends UpdateCommand

  final case class UpdateName(name: String) extends UserUpdateCommand
  final case class UpdatePassword(password: UserPassword) extends UserUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, UserId, User]): UserRepository[F] =
    new InMemoryUserRepository[F](cache)

  def notion[F[_]: Functor](client: NotionUserClient[F])(implicit
      userAdapter: Adapter[User, NotionUserRequest, NotionUserResponse],
      userIdAdapter: Adapter[UserId, NotionUserId, NotionUserId],
  ) = new NotionUserRepository[F](client)
}
