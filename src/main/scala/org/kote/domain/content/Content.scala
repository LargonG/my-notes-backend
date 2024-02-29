package org.kote.domain.content

import org.kote.domain.content.File.FileId

final case class Content(
    text: List[String],
    files: List[FileId],
)
