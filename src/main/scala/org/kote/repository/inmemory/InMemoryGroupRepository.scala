package org.kote.repository.inmemory

import cats.Monad
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group
import org.kote.domain.group.Group.GroupId
import org.kote.repository.GroupRepository
import org.kote.repository.GroupRepository.GroupUpdateCommand

class InMemoryGroupRepository[F[_]: Monad](cache: Cache[F, GroupId, Group])
    extends GroupRepository[F] {
  override def create(group: Group): F[Long] = cache.add(group.id, group).as(1L)

  override def list(boardId: BoardId): OptionT[F, List[Group]] =
    OptionT.liftF(cache.values.map(_.filter(_.boardId == boardId)))

  override def get(id: GroupId): OptionT[F, Group] = OptionT(cache.get(id))

  override def delete(id: GroupId): OptionT[F, Group] = OptionT(cache.remove(id))

  override def update(
      id: GroupId,
      cmds: GroupUpdateCommand*,
  ): OptionT[F, Group] =
    cacheUpdateAndGet(id, cmds, GroupRepository.standardUpdateGroup, get, cache)
}
