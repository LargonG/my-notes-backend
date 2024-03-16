package org.kote.domain.integration.notion

import org.kote.client.notion.NotionUserId
import org.kote.domain.user.User.UserId

final case class NotionUserIntegration(
    userId: UserId,
    notionUserId: NotionUserId,
)
