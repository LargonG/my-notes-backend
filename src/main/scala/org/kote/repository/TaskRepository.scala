package org.kote.repository

import cats.Monad
import cats.data.OptionT
import cats.effect.kernel.Clock
import org.kote.common.cache.Cache
import org.kote.domain.board.Board.BoardId
import org.kote.domain.content.Content
import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task
import org.kote.domain.task.Task.{Status, TaskId}
import org.kote.domain.user.User.UserId
import org.kote.repository.TaskRepository.TaskUpdateCommand
import org.kote.repository.inmemory.InMemoryTaskRepository

/** Описывает хранилище задач
  * @tparam F
  *   side-effects
  */
trait TaskRepository[F[_]] extends UpdatableRepository[F, Task, TaskId, TaskUpdateCommand] {
  def all: F[List[Task]]

  def listByGroup(groupId: GroupId): OptionT[F, List[Task]]

  def listByBoard(boardId: BoardId): OptionT[F, List[Task]]
}

object TaskRepository {
  sealed trait TaskUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends TaskUpdateCommand
  final case class UpdateGroup(groupId: GroupId) extends TaskUpdateCommand
  final case class UpdateAssigns(assigns: List[UserId]) extends TaskUpdateCommand
  final case class AddAssign(user: UserId) extends TaskUpdateCommand
  final case class RemoveAssign(user: UserId) extends TaskUpdateCommand
  final case class UpdateStatus(status: Status) extends TaskUpdateCommand
  final case class UpdateContent(content: Content) extends TaskUpdateCommand

  def inMemory[F[_]: Monad: Clock](cache: Cache[F, TaskId, Task]): TaskRepository[F] =
    new InMemoryTaskRepository[F](cache)

  private[repository] def standardUpdateLoop(task: Task, cmd: TaskUpdateCommand): Task = cmd match {
    // Здесь у нас человеческие масштабы коллекций,
    // поэтому можем позволить себе использовать O(n) операции
    case TaskRepository.UpdateTitle(title) =>
      task.copy(title = title)
    case TaskRepository.UpdateGroup(groupId) =>
      task.copy(group = groupId)
    case TaskRepository.UpdateAssigns(assigns) =>
      task.copy(assigns = assigns)
    case TaskRepository.AddAssign(user) =>
      if (task.assigns.contains(user))
        task
      else
        task.copy(assigns = user :: task.assigns)
    case TaskRepository.RemoveAssign(user) =>
      task.copy(assigns = task.assigns.filterNot(_ == user))
    case TaskRepository.UpdateStatus(status) =>
      task.copy(status = status)
    case TaskRepository.UpdateContent(content) =>
      task.copy(content = content)
  }
}
