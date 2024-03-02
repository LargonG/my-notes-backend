package org.kote.service

import cats.Monad
import cats.data.OptionT
import cats.effect.kernel.Clock
import cats.effect.std.UUIDGen
import cats.implicits.toTraverseOps
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.{Status, TaskId}
import org.kote.domain.task.{CreateTask, Task, TaskResponse}
import org.kote.repository.TaskRepository.TaskUpdateCommand
import org.kote.repository.{BoardRepository, GroupRepository, TaskRepository}

trait TaskService[F[_]] {
  def create(createTask: CreateTask): F[TaskResponse]

  def list(boardId: BoardId): OptionT[F, List[TaskId]]

  def listByGroup(groupId: GroupId): OptionT[F, List[TaskId]]

  def listByStatus(boardId: BoardId, status: Status): OptionT[F, List[TaskId]]

  def get(id: TaskId): OptionT[F, TaskResponse]

  def update(id: TaskId, cmds: List[TaskUpdateCommand]): OptionT[F, TaskResponse]

  def delete(id: TaskId): OptionT[F, TaskResponse]
}

final case class RepositoryTaskService[F[_]: UUIDGen: Monad: Clock](
    boardRepository: BoardRepository[F],
    groupRepository: GroupRepository[F],
    taskRepository: TaskRepository[F],
) extends TaskService[F] {

  override def create(createTask: CreateTask): F[TaskResponse] =
    for {
      uuid <- UUIDGen[F].randomUUID
      time <- Clock[F].realTimeInstant
      task = Task.fromCreateTask(uuid, time, createTask)
      _ <- taskRepository.create(task)
    } yield task.toResponse

  override def list(boardId: BoardId): OptionT[F, List[TaskId]] =
    for {
      board <- boardRepository.get(boardId)
      lists <- board.groups.traverse(id => listByGroup(id))
    } yield lists.flatten

  override def listByGroup(groupId: GroupId): OptionT[F, List[TaskId]] =
    for {
      group <- groupRepository.get(groupId)
    } yield group.tasks

  override def listByStatus(boardId: BoardId, status: Status): OptionT[F, List[TaskId]] =
    list(boardId)
      .map(tasks =>
        tasks.traverse(id => get(id).map(response => (response.id, response.status == status))),
      )
      .flatten
      .map(list => list.filter(_._2).map(_._1))

  override def get(id: TaskId): OptionT[F, TaskResponse] =
    taskRepository.get(id).map(_.toResponse)

  override def update(id: TaskId, cmds: List[TaskUpdateCommand]): OptionT[F, TaskResponse] =
    taskRepository.update(id, cmds).map(_.toResponse)

  override def delete(id: TaskId): OptionT[F, TaskResponse] =
    taskRepository.delete(id).map(_.toResponse)
}
