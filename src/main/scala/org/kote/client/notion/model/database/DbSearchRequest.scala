package org.kote.client.notion.model.database

import io.circe.Encoder
import org.kote.client.notion.model.filter.DatabaseFilter
import org.kote.client.notion.model.list.PaginatedList.Cursor

case class DbSearchRequest(
    query: Option[String],
    cursor: Option[Cursor],
)

object DbSearchRequest {
  implicit val encoder: Encoder[DbSearchRequest] =
    Encoder.forProduct4("query", "filter", "start_cursor", "page_size") { source =>
      (source.query, DatabaseFilter, source.cursor.map(_.value), source.cursor.map(_.pageSize))
    }
}
