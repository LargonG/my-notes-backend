package org.kote.repository

import cats.Monad
import org.kote.adapter.Adapter
import org.kote.client.notion.model.database.{DbId, DbResponse, DbUpdateRequest}
import org.kote.client.notion.{NotionDatabaseClient, NotionPageClient}
import org.kote.common.cache.Cache
import org.kote.domain.group.Group
import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.TaskId
import org.kote.repository.GroupRepository.GroupUpdateCommand
import org.kote.repository.inmemory.InMemoryGroupRepository
import org.kote.repository.notion.NotionGroupRepository

trait GroupRepository[F[_]] extends UpdatableRepository[F, Group, GroupId, GroupUpdateCommand]

object GroupRepository {
  sealed trait GroupUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends GroupUpdateCommand
  final case class AddTask(task: TaskId) extends GroupUpdateCommand
  final case class RemoveTask(task: TaskId) extends GroupUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, GroupId, Group]): GroupRepository[F] =
    new InMemoryGroupRepository[F](cache)

  def notion[F[_]: Monad](pageClient: NotionPageClient[F], dbClient: NotionDatabaseClient[F])(
      implicit
      groupAdapter: Adapter[Group, DbUpdateRequest, DbResponse],
      groupIdAdapter: Adapter[GroupId, DbId, DbId],
  ) = new NotionGroupRepository[F](pageClient, dbClient)
}
