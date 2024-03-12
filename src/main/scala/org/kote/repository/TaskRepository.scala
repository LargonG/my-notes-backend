package org.kote.repository

import cats.Monad
import cats.data.OptionT
import cats.effect.kernel.Clock
import org.kote.common.cache.Cache
import org.kote.domain.board.Board.BoardId
import org.kote.domain.comment.Comment.CommentId
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
  final case class UpdateAssigns(assigns: List[UserId]) extends TaskUpdateCommand
  final case class AddAssign(user: UserId) extends TaskUpdateCommand
  final case class RemoveAssign(user: UserId) extends TaskUpdateCommand
  final case class UpdateStatus(status: Status) extends TaskUpdateCommand
  final case class UpdateContent(content: Content) extends TaskUpdateCommand
  final case class AddComment(comment: CommentId) extends TaskUpdateCommand
  final case class DeleteComment(commentId: CommentId) extends TaskUpdateCommand

  def inMemory[F[_]: Monad: Clock](cache: Cache[F, TaskId, Task]): TaskRepository[F] =
    new InMemoryTaskRepository[F](cache)
}
