package org.kote.client.notion.model.file

import java.net.URL
import java.time.Instant

sealed trait File

final case class ExternalFile(url: URL)

final case class NotionFile(url: URL, exprityTime: Instant)
