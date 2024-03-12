package org.kote.repository

import cats.Monad
import cats.data.OptionT
import org.kote.common.cache.Cache
import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group
import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.TaskId
import org.kote.repository.GroupRepository.GroupUpdateCommand
import org.kote.repository.inmemory.InMemoryGroupRepository

trait GroupRepository[F[_]] extends UpdatableRepository[F, Group, GroupId, GroupUpdateCommand] {
  def list(boardId: BoardId): OptionT[F, List[Group]]
}

object GroupRepository {
  sealed trait GroupUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends GroupUpdateCommand
  final case class AddTask(task: TaskId) extends GroupUpdateCommand
  final case class RemoveTask(task: TaskId) extends GroupUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, GroupId, Group]): GroupRepository[F] =
    new InMemoryGroupRepository[F](cache)
}
