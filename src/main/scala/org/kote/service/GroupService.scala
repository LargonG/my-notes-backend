package org.kote.service

import cats.Monad
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.implicits.toTraverseOps
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.domain.group.Group.GroupId
import org.kote.domain.group.{CreateGroup, Group, GroupResponse}
import org.kote.domain.task.Task.TaskId
import org.kote.domain.task.TaskResponse
import org.kote.repository.{GroupRepository, TaskRepository}

trait GroupService[F[_]] {
  def create(createGroup: CreateGroup): OptionT[F, GroupResponse]

  def get(id: GroupId): OptionT[F, GroupResponse]

  def moveTask(to: GroupId, what: TaskId): OptionT[F, TaskResponse]

  def delete(id: GroupId): OptionT[F, GroupResponse]
}

object GroupService {
  def fromRepository[F[_]: UUIDGen: Monad](
      groupRepository: GroupRepository[F],
      taskRepository: TaskRepository[F],
  ): GroupService[F] =
    new RepositoryGroupService[F](groupRepository, taskRepository)
}

class RepositoryGroupService[F[_]: UUIDGen: Monad](
    groupRepository: GroupRepository[F],
    taskRepository: TaskRepository[F],
) extends GroupService[F] {
  override def create(createGroup: CreateGroup): OptionT[F, GroupResponse] =
    OptionT.liftF(for {
      uuid <- UUIDGen[F].randomUUID
      group = Group.fromCreateGroup(uuid, createGroup)
      _ <- groupRepository.create(group)
    } yield group.toResponse)

  override def get(id: GroupId): OptionT[F, GroupResponse] =
    groupRepository.get(id).map(_.toResponse)

  override def moveTask(to: GroupId, what: TaskId): OptionT[F, TaskResponse] =
    taskRepository.update(what, TaskRepository.UpdateGroup(to)).map(_.toResponse)

  override def delete(id: GroupId): OptionT[F, GroupResponse] =
    for {
      deleted <- groupRepository.delete(id)
      tasks <- taskRepository.listByGroup(id)
      _ <- tasks.traverse(task => taskRepository.delete(task.id))
    } yield deleted.toResponse
}
