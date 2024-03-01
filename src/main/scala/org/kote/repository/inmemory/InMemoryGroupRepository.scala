package org.kote.repository.inmemory

import cats.Monad
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.group.Group
import org.kote.domain.group.Group.GroupId
import org.kote.repository.GroupRepository
import org.kote.repository.GroupRepository.GroupUpdateCommand

class InMemoryGroupRepository[F[_]: Monad](cache: Cache[F, GroupId, Group])
    extends GroupRepository[F] {
  override def create(group: Group): F[Long] = cache.add(group.id, group).as(1L)

  override def list: F[List[Group]] = cache.values

  override def get(id: GroupId): OptionT[F, Group] = OptionT(cache.get(id))

  override def delete(id: GroupId): OptionT[F, Group] = OptionT(cache.remove(id))

  override def update(
      id: GroupId,
      cmds: List[GroupUpdateCommand],
  ): OptionT[F, Group] = {
    def loop(group: Group, cmd: GroupUpdateCommand): Group = cmd match {
      case GroupRepository.UpdateTitle(title) =>
        group.copy(title = title)
      case GroupRepository.AddTask(task) =>
        group.copy(tasks = task :: group.tasks)
      case GroupRepository.RemoveTask(task) =>
        group.copy(tasks = group.tasks.filterNot(_ == task))
    }

    cacheUpdateAndGet(id, cmds, loop, get, cache)
  }
}
