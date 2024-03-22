package org.kote.domain.integration.notion

import org.kote.client.notion.NotionDatabaseId
import org.kote.domain.board.Board.BoardId

final case class NotionDatabaseIntegration(
    boardId: BoardId,
    notionDatabaseId: NotionDatabaseId,
)
