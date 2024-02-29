package org.kote.domain.content

import org.kote.domain.content.File.FileId

import java.util.UUID

final case class File(
    id: FileId,
)

object File {
  final case class FileId(inner: UUID) extends AnyVal
}
