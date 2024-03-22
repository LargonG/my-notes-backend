package org.kote.repository.inmemory

import cats.Monad
import cats.data.OptionT
import cats.effect.Clock
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.board.Board
import org.kote.domain.group.Group
import org.kote.domain.task.Task
import org.kote.domain.task.Task.TaskId
import org.kote.repository.TaskRepository
import org.kote.repository.TaskRepository.TaskUpdateCommand

class InMemoryTaskRepository[F[_]: Monad: Clock](cache: Cache[F, TaskId, Task])
    extends TaskRepository[F] {
  override def create(task: Task): F[Long] = cache.add(task.id, task).as(1L)

  override def all: F[List[Task]] = cache.values

  override def listByGroup(groupId: Group.GroupId): OptionT[F, List[Task]] =
    OptionT.liftF(cache.values.map(_.filter(_.group == groupId)))

  override def listByBoard(boardId: Board.BoardId): OptionT[F, List[Task]] =
    OptionT.liftF(cache.values.map(_.filter(_.board == boardId)))

  override def get(id: TaskId): OptionT[F, Task] = OptionT(cache.get(id))

  override def delete(id: TaskId): OptionT[F, Task] = OptionT(cache.remove(id))

  override def update(id: TaskId, cmds: TaskUpdateCommand*): OptionT[F, Task] =
    for {
      time <- OptionT.liftF(Clock[F].realTimeInstant)
      res <- cacheUpdateAndGet(
        id,
        cmds,
        TaskRepository.standardUpdateLoop,
        get,
        cache,
        { task: Task => task.copy(updatedAt = time) },
      )
    } yield res

}
