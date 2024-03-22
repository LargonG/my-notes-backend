package org.kote.repository

import cats.Monad
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache

package object inmemory {
  private[repository] def cacheUpdateAndGet[F[_]: Monad, T, ID, CMD](
      id: ID,
      cmds: Seq[CMD],
      loop: (T, CMD) => T,
      get: ID => OptionT[F, T],
      cache: Cache[F, ID, T],
      fix: T => T = identity[T](_),
  ): OptionT[F, T] = cacheUpdate(id, cmds, loop, get, cache, fix, (fa, _, res) => fa.as(res))

  private[repository] def cacheGetAndUpdate[F[_]: Monad, T, ID, CMD](
      id: ID,
      cmds: Seq[CMD],
      loop: (T, CMD) => T,
      get: ID => OptionT[F, T],
      cache: Cache[F, ID, T],
      fix: T => T = identity[T](_),
  ): OptionT[F, T] = cacheUpdate(id, cmds, loop, get, cache, fix, (fa, res, _) => fa.as(res))

  private def cacheUpdate[F[_]: Monad, T, ID, CMD](
      id: ID,
      cmds: Seq[CMD],
      loop: (T, CMD) => T,
      get: ID => OptionT[F, T],
      cache: Cache[F, ID, T],
      fix: T => T,
      returns: (F[Unit], T, T) => F[T],
  ): OptionT[F, T] =
    for {
      previous <- get(id)
      updated = fix(cmds.foldLeft(previous)(loop))
      res <- OptionT.liftF(returns(cache.update(id, updated), previous, updated))
    } yield res
}
