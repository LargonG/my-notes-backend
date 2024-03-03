package org.kote.service

import cats.Monad
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.implicits.toTraverseOps
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.domain.content.file.File.FileId
import org.kote.domain.content.file.{CreateFile, File, FileResponse}
import org.kote.domain.task.Task.TaskId
import org.kote.repository.{FileRepository, TaskRepository}

trait FileService[F[_]] {
  def create(createFile: CreateFile): F[FileResponse]

  def list(taskId: TaskId): OptionT[F, List[FileResponse]]

  def get(id: FileId): OptionT[F, FileResponse]

  def delete(id: FileId): OptionT[F, FileResponse]
}

object FileService {
  def fromRepository[F[_]: UUIDGen: Monad](
      taskRepository: TaskRepository[F],
      fileRepository: FileRepository[F],
  ): FileService[F] =
    new RepositoryFileService[F](taskRepository, fileRepository)
}

class RepositoryFileService[F[_]: UUIDGen: Monad](
    taskRepository: TaskRepository[F],
    fileRepository: FileRepository[F],
) extends FileService[F] {
  override def create(createFile: CreateFile): F[FileResponse] =
    for {
      uuid <- UUIDGen[F].randomUUID
      file = File.fromCreateFile(uuid, createFile)
      _ <- fileRepository.create(file)
      // todo: update task ??? or create link function?
    } yield file.toResponse

  override def list(taskId: TaskId): OptionT[F, List[FileResponse]] =
    taskRepository
      .get(taskId)
      .flatMap(task => task.content.files.traverse(id => get(id)))

  override def get(id: FileId): OptionT[F, FileResponse] =
    fileRepository.get(id).map(_.toResponse)

  override def delete(id: FileId): OptionT[F, FileResponse] =
    fileRepository
      .get(id)
      .map(_.toResponse) // todo: update all tasks, where it is linked ??? should we?
}
