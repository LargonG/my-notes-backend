package org.kote.repository

import cats.Monad
import cats.data.OptionT
import org.kote.common.cache.Cache
import org.kote.repository.inmemory.InMemoryIntegrationRepository

trait IntegrationRepository[F[_], IN, OUT] {
  def set(key: IN, value: OUT): F[Long]
  def getByKey(key: IN): OptionT[F, OUT]
  def getByValue(value: OUT): OptionT[F, IN]
  def delete(key: IN): OptionT[F, OUT]
}

object IntegrationRepository {
  def inMemory[F[_]: Monad, K, V](
      cacheIn: Cache[F, K, V],
      cacheOut: Cache[F, V, K],
  ): IntegrationRepository[F, K, V] =
    new InMemoryIntegrationRepository(cacheIn, cacheOut)
}
