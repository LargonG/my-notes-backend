package org.kote.repository.inmemory

import cats.Functor
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.common.cache.Cache
import org.kote.domain.content.file.File
import org.kote.domain.content.file.File.FileId
import org.kote.repository.FileRepository

class InMemoryFileRepository[F[_]: Functor](cache: Cache[F, FileId, File])
    extends FileRepository[F] {
  override def create(file: File): F[Long] =
    cache.add(file.id, file).as(1L)

  override def list: F[List[File]] =
    cache.values

  override def get(id: FileId): OptionT[F, File] = OptionT(cache.get(id))

  override def delete(id: FileId): OptionT[F, File] = OptionT(cache.remove(id))
}
