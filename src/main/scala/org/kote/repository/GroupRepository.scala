package org.kote.repository

import cats.Monad
import cats.data.OptionT
import org.kote.common.cache.Cache
import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group
import org.kote.domain.group.Group.GroupId
import org.kote.repository.GroupRepository.GroupUpdateCommand
import org.kote.repository.inmemory.InMemoryGroupRepository

trait GroupRepository[F[_]] extends UpdatableRepository[F, Group, GroupId, GroupUpdateCommand] {
  def list(boardId: BoardId): OptionT[F, List[Group]]
}

object GroupRepository {
  sealed trait GroupUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends GroupUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, GroupId, Group]): GroupRepository[F] =
    new InMemoryGroupRepository[F](cache)

  private[repository] def standardUpdateGroup(group: Group, cmd: GroupUpdateCommand): Group =
    cmd match {
      case GroupRepository.UpdateTitle(title) =>
        group.copy(title = title)
    }
}
