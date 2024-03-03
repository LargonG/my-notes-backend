package org.kote.common.filename

import org.kote.domain.content.file.File.FileId

import java.util.UUID

trait FileName[T] {
  def getFileName(value: T): String
}

object FileName {
  def apply[T: FileName]: FileName[T] = implicitly

  final implicit class ToFileName[T: FileName](value: T) {
    def getFileName: String = FileName[T].getFileName(value)
  }

  implicit val stringFileName: FileName[String] = identity
  implicit val uuidFileName: FileName[UUID] = _.toString

  implicit val fileIdFilename: FileName[FileId] =
    fileId => uuidFileName.getFileName(fileId.inner)
}
