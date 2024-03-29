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
import org.kote.repository.TaskRepository

trait TaskService[F[_]] {

  /** Создаёт задачу на какой-то доске, в какой-то группе.
    *
    * @param createTask
    *   запрос создания задачи
    * @return
    *   новую задачу
    */
  def create(createTask: CreateTask): OptionT[F, TaskResponse]

  def list(boardId: BoardId): OptionT[F, List[TaskResponse]]

  def listByGroup(groupId: GroupId): OptionT[F, List[TaskResponse]]

  def listByStatus(boardId: BoardId, status: Status): OptionT[F, List[TaskResponse]]

  def get(id: TaskId): OptionT[F, TaskResponse]

  def delete(id: TaskId): OptionT[F, TaskResponse]
}

object TaskService {
  def fromRepository[F[_]: UUIDGen: Monad: Clock](
      taskRepository: TaskRepository[F],
  ): TaskService[F] =
    new RepositoryTaskService[F](taskRepository)
}

class RepositoryTaskService[F[_]: UUIDGen: Monad: Clock](
    taskRepository: TaskRepository[F],
) extends TaskService[F] {

  override def create(createTask: CreateTask): OptionT[F, TaskResponse] =
    OptionT.liftF(for {
      uuid <- UUIDGen[F].randomUUID
      time <- Clock[F].realTimeInstant
      task = Task.fromCreateTask(uuid, time, createTask)
      _ <- taskRepository.create(task)
    } yield task.toResponse)

  override def listByStatus(boardId: BoardId, status: Status): OptionT[F, List[TaskResponse]] =
    list(boardId)
      .map(tasks =>
        tasks.traverse(response =>
          get(response.id).map(response => (response, response.status == status)),
        ),
      )
      .flatten
      .map(list => list.filter(_._2).map(_._1))

  override def list(boardId: BoardId): OptionT[F, List[TaskResponse]] =
    taskRepository.listByBoard(boardId).map(_.map(_.toResponse))

  override def listByGroup(groupId: GroupId): OptionT[F, List[TaskResponse]] =
    taskRepository.listByGroup(groupId).map(_.map(_.toResponse))

  override def get(id: TaskId): OptionT[F, TaskResponse] =
    taskRepository.get(id).map(_.toResponse)

  override def delete(id: TaskId): OptionT[F, TaskResponse] =
    for {
      deleted <- taskRepository.delete(id)
    } yield deleted.toResponse
}
