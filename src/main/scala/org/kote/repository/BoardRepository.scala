package org.kote.repository

import cats.Monad
import cats.data.OptionT
import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor
import org.kote.common.cache.Cache
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User.UserId
import org.kote.repository.BoardRepository.BoardUpdateCommand
import org.kote.repository.inmemory.InMemoryBoardRepository
import org.kote.repository.postgresql.BoardRepositoryPostgresql

trait BoardRepository[F[_]] extends UpdatableRepository[F, Board, BoardId, BoardUpdateCommand] {
  def all: F[List[Board]]

  def list(userId: UserId): OptionT[F, List[Board]]
}

object BoardRepository {
  sealed trait BoardUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends BoardUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, BoardId, Board]): BoardRepository[F] =
    new InMemoryBoardRepository[F](cache)

  def postgres[F[_]: MonadCancelThrow](implicit tr: Transactor[F]): BoardRepository[F] =
    new BoardRepositoryPostgresql[F]

  private[repository] def standardUpdateLoop(board: Board, cmd: BoardUpdateCommand): Board =
    cmd match {
      case BoardRepository.UpdateTitle(title) =>
        board.copy(title = title)
    }
}
