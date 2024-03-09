package org.kote.client.notion.model.page

import io.circe.Encoder
import org.kote.client.notion.model.filter.PageFilter
import org.kote.client.notion.model.list.PaginatedList.Cursor
case class PageSearchRequest(
    query: Option[String],
    cursor: Option[Cursor],
)

object PageSearchRequest {
  implicit val encoder: Encoder[PageSearchRequest] =
    Encoder.forProduct4("query", "filter", "start_cursor", "page_size") { source =>
      (source.query, PageFilter, source.cursor.map(_.value), source.cursor.map(_.pageSize))
    }
}
