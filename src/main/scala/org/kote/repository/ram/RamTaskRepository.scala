package org.kote.repository.ram

import cats.Monad
import cats.data.OptionT
import cats.effect.Clock
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.task.Task
import org.kote.domain.task.Task.TaskId
import org.kote.repository.TaskRepository
import org.kote.repository.TaskRepository.TaskUpdateCommand

class RamTaskRepository[F[_]: Monad: Clock](cache: Cache[F, TaskId, Task])
    extends TaskRepository[F] {
  override def create(task: Task): F[Long] = cache.add(task.id, task).as(1L)

  override def list: F[List[Task]] = cache.values

  override def get(id: TaskId): OptionT[F, Task] = OptionT(cache.get(id))

  override def delete(id: TaskId): OptionT[F, Task] = OptionT(cache.remove(id))

  override def update(id: TaskId, cmds: List[TaskUpdateCommand]): OptionT[F, Task] = {
    def loop(task: Task, cmd: TaskUpdateCommand): Task = cmd match {
      // Здесь у нас человеческие масштабы коллекций,
      // поэтому можем позволить себе использовать O(n) операции
      case TaskRepository.UpdateTitle(title) =>
        task.copy(title = title)
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
      case TaskRepository.AddComment(comment) =>
        if (task.comments.contains(comment))
          task
        else
          task.copy(comments = comment :: task.comments)
      case TaskRepository.DeleteComment(commentId) =>
        task.copy(comments = task.comments.filterNot(_ == commentId))
    }

    for {
      time <- OptionT.liftF(Clock[F].realTimeInstant)
      res <- cacheUpdateAndGet(
        id,
        cmds,
        loop,
        get,
        cache,
        { task: Task => task.copy(updatedAt = time) },
      )
    } yield res
  }
}
