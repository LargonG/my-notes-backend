package org.kote.repository.inmemory

import cats.Monad
import cats.data.OptionT
import cats.implicits.toFunctorOps
import cats.syntax.flatMap._
import org.kote.common.cache.Cache
import org.kote.repository.IntegrationRepository

class InMemoryIntegrationRepository[F[_]: Monad, IN, OUT](
    cacheIn: Cache[F, IN, OUT],
    cacheOut: Cache[F, OUT, IN],
) extends IntegrationRepository[F, IN, OUT] {
  override def set(key: IN, value: OUT): F[Long] =
    for {
      _ <- cacheIn.add(key, value)
      _ <- cacheOut.add(value, key)
    } yield 1L

  override def getByKey(key: IN): OptionT[F, OUT] =
    OptionT(cacheIn.get(key))

  override def getByValue(value: OUT): OptionT[F, IN] =
    OptionT(cacheOut.get(value))

  override def delete(key: IN): OptionT[F, OUT] =
    for {
      value <- OptionT(cacheIn.remove(key))
      _ <- OptionT(cacheOut.remove(value))
    } yield value
}
