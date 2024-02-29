package org.kote.repository

import org.kote.domain.group.Group
import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.TaskId
import org.kote.repository.GroupRepository.GroupUpdateCommand

trait GroupRepository[F[_]] extends UpdatableRepository[F, Group, GroupId, GroupUpdateCommand]

object GroupRepository {
  sealed trait GroupUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends GroupUpdateCommand
  final case class AddTask(task: TaskId) extends GroupUpdateCommand
  final case class RemoveTask(task: TaskId) extends GroupUpdateCommand
}
