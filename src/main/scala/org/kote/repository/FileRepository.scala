package org.kote.repository

import org.kote.domain.content.File
import org.kote.domain.content.File.FileId

trait FileRepository[F[_]] extends Repository[F, File, FileId]
