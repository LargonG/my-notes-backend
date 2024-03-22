package org.kote.domain.integration.notion

import org.kote.client.notion.NotionPageId
import org.kote.domain.user.User.UserId

final case class NotionMainPageIntegration(
    userId: UserId,
    mainPage: NotionPageId,
)
