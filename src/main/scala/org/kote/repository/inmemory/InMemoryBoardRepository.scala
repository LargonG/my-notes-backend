package org.kote.repository.inmemory

import cats.Monad
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.repository.BoardRepository
import org.kote.repository.BoardRepository.BoardUpdateCommand

class InMemoryBoardRepository[F[_]: Monad](cache: Cache[F, BoardId, Board])
    extends BoardRepository[F] {

  override def create(board: Board): F[Long] = cache.add(board.id, board).as(1L)

  override def list: F[List[Board]] = cache.values

  override def get(id: BoardId): OptionT[F, Board] = OptionT(cache.get(id))

  override def delete(id: BoardId): OptionT[F, Board] = OptionT(cache.remove(id))

  override def update(
      id: BoardId,
      cmds: List[BoardUpdateCommand],
  ): OptionT[F, Board] = {
    def loop(board: Board, cmd: BoardUpdateCommand): Board = cmd match {
      case BoardRepository.UpdateTitle(title) =>
        board.copy(title = title)
    }

    cacheUpdateAndGet(id, cmds, loop, get, cache)
  }
}
