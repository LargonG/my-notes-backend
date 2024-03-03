package org.kote.repository

import cats.Monad
import org.kote.common.cache.Cache
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.repository.BoardRepository.BoardUpdateCommand
import org.kote.repository.inmemory.InMemoryBoardRepository

trait BoardRepository[F[_]] extends UpdatableRepository[F, Board, BoardId, BoardUpdateCommand] {}

object BoardRepository {
  sealed trait BoardUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends BoardUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, BoardId, Board]): BoardRepository[F] =
    new InMemoryBoardRepository[F](cache)
}
