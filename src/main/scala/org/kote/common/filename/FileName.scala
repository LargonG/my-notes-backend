package org.kote.common.filename

trait FileName[T] {
  def getFileName(value: T): String
}

object FileName {
  def apply[T: FileName]: FileName[T] = implicitly

  final implicit class ToFileName[T: FileName](value: T) {
    def getFileName: String = FileName[T].getFileName(value)
  }
}
