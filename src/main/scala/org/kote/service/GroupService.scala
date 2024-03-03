package org.kote.service

import cats.Monad
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.domain.group.Group.GroupId
import org.kote.domain.group.{CreateGroup, Group, GroupResponse}
import org.kote.domain.task.Task.TaskId
import org.kote.repository.GroupRepository
import org.kote.repository.GroupRepository.GroupUpdateCommand

trait GroupService[F[_]] {
  def create(createGroup: CreateGroup): F[GroupResponse]

  def get(id: GroupId): OptionT[F, GroupResponse]

  def moveTask(from: GroupId, to: GroupId, what: TaskId): OptionT[F, String]

  def update(id: GroupId, cmds: List[GroupUpdateCommand]): OptionT[F, GroupResponse]

  def delete(id: GroupId): OptionT[F, GroupResponse]
}

object GroupService {
  def fromRepository[F[_]: UUIDGen: Monad](
      groupRepository: GroupRepository[F],
  ): GroupService[F] =
    new RepositoryGroupService[F](groupRepository)
}

class RepositoryGroupService[F[_]: UUIDGen: Monad](
    groupRepository: GroupRepository[F],
) extends GroupService[F] {
  override def create(createGroup: CreateGroup): F[GroupResponse] =
    for {
      uuid <- UUIDGen[F].randomUUID
      group = Group.fromCreateGroup(uuid, createGroup)
      _ <- groupRepository.create(group)
      // todo: update board
    } yield group.toResponse

  override def get(id: GroupId): OptionT[F, GroupResponse] =
    groupRepository.get(id).map(_.toResponse)

  override def moveTask(from: GroupId, to: GroupId, what: TaskId): OptionT[F, String] =
    for {
      _ <- groupRepository.update(from, GroupRepository.RemoveTask(what))
      _ <- groupRepository.update(to, GroupRepository.AddTask(what))
    } yield "moved"

  override def update(id: GroupId, cmds: List[GroupUpdateCommand]): OptionT[F, GroupResponse] =
    groupRepository.update(id, cmds).map(_.toResponse)

  override def delete(id: GroupId): OptionT[F, GroupResponse] =
    groupRepository.delete(id).map(_.toResponse) // todo: update board
}
