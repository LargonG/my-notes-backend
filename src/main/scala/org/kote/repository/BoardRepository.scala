package org.kote.repository

import cats.Monad
import cats.data.{NonEmptyList, OptionT}
import cats.effect.kernel.Async
import org.kote.common.cache.Cache
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group.GroupId
import org.kote.domain.user.User.UserId
import org.kote.repository.BoardRepository.BoardUpdateCommand
import org.kote.repository.inmemory.InMemoryBoardRepository
import org.kote.repository.mixers.MixerBoardRepository

trait BoardRepository[F[_]] extends UpdatableRepository[F, Board, BoardId, BoardUpdateCommand] {
  def all: F[List[Board]]

  def list(userId: UserId): OptionT[F, List[Board]]
}

object BoardRepository {
  sealed trait BoardUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends BoardUpdateCommand
  final case class ChangeGroups(groups: List[GroupId]) extends BoardUpdateCommand
  final case class AddGroup(groupId: GroupId) extends BoardUpdateCommand
  final case class RemoveGroup(groupId: GroupId) extends BoardUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, BoardId, Board]): BoardRepository[F] =
    new InMemoryBoardRepository[F](cache)

  def mix[F[_]: Async](
      main: BoardRepository[F],
      other: BoardRepository[F]*,
  ): BoardRepository[F] =
    new MixerBoardRepository[F](NonEmptyList(main, other.toList))
}
