package org.kote.repository.inmemory

import cats.Monad
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.user.User
import org.kote.domain.user.User.UserId
import org.kote.repository.UserRepository
import org.kote.repository.UserRepository.UserUpdateCommand

class InMemoryUserRepository[F[_]: Monad](cache: Cache[F, UserId, User]) extends UserRepository[F] {
  override def create(user: User): F[Long] = cache.add(user.id, user).as(1L)

  override def all: F[List[User]] = cache.values

  override def get(id: UserId): OptionT[F, User] = OptionT(cache.get(id))

  override def delete(id: UserId): OptionT[F, User] = OptionT(cache.remove(id))

  override def update(id: UserId, cmds: UserUpdateCommand*): OptionT[F, User] =
    cacheUpdateAndGet(id, cmds, UserRepository.standardUpdateLoop, get, cache)
}
