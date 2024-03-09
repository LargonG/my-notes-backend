package org.kote.repository

import cats.Functor
import cats.data.OptionT
import org.kote.common.cache.Cache
import org.kote.domain.content.file.File
import org.kote.domain.content.file.File.FileId
import org.kote.domain.task.Task.TaskId
import org.kote.repository.inmemory.InMemoryFileRepository

trait FileRepository[F[_]] extends Repository[F, File, FileId] {
  def list(taskId: TaskId): OptionT[F, List[File]]
}

object FileRepository {
  def inMemory[F[_]: Functor](cache: Cache[F, FileId, File]): FileRepository[F] =
    new InMemoryFileRepository[F](cache)
}
