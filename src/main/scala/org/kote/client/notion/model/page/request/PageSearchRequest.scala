package org.kote.client.notion.model.page.request

import io.circe.{Encoder, Json}
import org.kote.client.notion
import org.kote.client.notion.model.filter.PageFilter
import org.kote.client.notion.model.list.PaginatedList.Cursor
case class PageSearchRequest(
    query: Option[String],
    cursor: Option[Cursor],
)

object PageSearchRequest {
  implicit val encoder: Encoder[PageSearchRequest] =
    Encoder.instance { source: PageSearchRequest =>
      Json.obj(
        List(
          notion.optionEncode("query", source.query),
          notion.optionEncode("filter", Some(PageFilter)),
          notion.optionEncode("start_cursor", source.cursor.map(_.value)),
          notion.optionEncode("page_size", source.cursor.map(_.pageSize)),
        ).flatMap {
          case Some(value) => List(value)
          case None        => List.empty
        }: _*,
      )
    }
}
