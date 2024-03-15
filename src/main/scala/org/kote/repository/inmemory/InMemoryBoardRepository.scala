package org.kote.repository.inmemory

import cats.Monad
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User
import org.kote.repository.BoardRepository
import org.kote.repository.BoardRepository.BoardUpdateCommand

class InMemoryBoardRepository[F[_]: Monad](cache: Cache[F, BoardId, Board])
    extends BoardRepository[F] {

  override def create(board: Board): F[Long] = cache.add(board.id, board).as(1L)

  override def all: F[List[Board]] = cache.values

  override def list(userId: User.UserId): OptionT[F, List[Board]] =
    OptionT.liftF(cache.values.map(_.filter(_.owner == userId)))

  override def get(id: BoardId): OptionT[F, Board] = OptionT(cache.get(id))

  override def delete(id: BoardId): OptionT[F, Board] = OptionT(cache.remove(id))

  override def update(
      id: BoardId,
      cmds: BoardUpdateCommand*,
  ): OptionT[F, Board] =
    cacheUpdateAndGet(id, cmds, BoardRepository.standardUpdateLoop, get, cache)
}
