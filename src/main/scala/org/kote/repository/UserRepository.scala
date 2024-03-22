package org.kote.repository

import cats.Monad
import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor
import org.kote.common.cache.Cache
import org.kote.domain.user.User
import org.kote.domain.user.User.{UserId, UserPassword}
import org.kote.repository.UserRepository.UserUpdateCommand
import org.kote.repository.inmemory.InMemoryUserRepository
import org.kote.repository.postgresql.UserRepositoryPostgresql

trait UserRepository[F[_]] extends UpdatableRepository[F, User, UserId, UserUpdateCommand] {
  def all: F[List[User]]
}

object UserRepository {
  sealed trait UserUpdateCommand extends UpdateCommand

  final case class UpdateName(name: String) extends UserUpdateCommand
  final case class UpdatePassword(password: UserPassword) extends UserUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, UserId, User]): UserRepository[F] =
    new InMemoryUserRepository[F](cache)

  def postgres[F[_]: MonadCancelThrow](implicit tr: Transactor[F]): UserRepository[F] =
    new UserRepositoryPostgresql[F]

  private[repository] def standardUpdateLoop(user: User, cmd: UserUpdateCommand): User = cmd match {
    case UserRepository.UpdateName(name) =>
      user.copy(name = name)
    case UserRepository.UpdatePassword(password) =>
      user.copy(password = password)
  }
}
