package org.kote.controller

import org.kote.common.controller.Controller
import org.kote.domain.content.file.File.FileId
import org.kote.domain.content.file.{CreateFile, FileResponse}
import org.kote.domain.task.Task.TaskId
import org.kote.service.FileService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint

class FileController[F[_]](fileService: FileService[F]) extends Controller[F] {
  private val standardPath: EndpointInput[Unit] = "api" / "v1" / "file"
  private val standardPathWithFileId: EndpointInput[FileId] = standardPath / path[FileId]("fileId")

  private val createFile: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать файл")
      .in(standardPath)
      .in(jsonBody[CreateFile])
      .out(jsonBody[FileResponse])
      .serverLogicSuccess(fileService.create)

  private val listFiles: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список файлов по таске")
      .in(standardPath / query[TaskId]("task_id"))
      .out(jsonBody[Option[List[FileResponse]]])
      .serverLogicSuccess(fileService.list(_).value)

  private val getFile: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить файл")
      .in(standardPathWithFileId)
      .out(jsonBody[Option[FileResponse]])
      .serverLogicSuccess(fileService.get(_).value)

  private val deleteFile: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить файл")
      .in(standardPathWithFileId)
      .out(jsonBody[Option[FileResponse]])
      .serverLogicSuccess(fileService.delete(_).value)

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(createFile, listFiles, getFile, deleteFile).map(_.withTag("File"))
}

object FileController {
  def make[F[_]](fileService: FileService[F]): FileController[F] =
    new FileController[F](fileService)
}
