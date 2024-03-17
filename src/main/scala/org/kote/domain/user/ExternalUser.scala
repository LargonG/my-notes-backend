package org.kote.domain.user

import org.kote.client.notion.NotionUserId

sealed trait ExternalUser

final case class NotionUser(id: Option[NotionUserId], name: Option[String]) extends ExternalUser
