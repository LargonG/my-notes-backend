package org.kote.repository

import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.repository.BoardRepository.BoardUpdateCommand

trait BoardRepository[F[_]] extends UpdatableRepository[F, Board, BoardId, BoardUpdateCommand] {}

object BoardRepository {
  sealed trait BoardUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends BoardUpdateCommand
}
