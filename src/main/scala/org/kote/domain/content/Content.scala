package org.kote.domain.content

import org.kote.domain.content.file.File.FileId

final case class Content(
    text: String,
    files: List[FileId],
)
