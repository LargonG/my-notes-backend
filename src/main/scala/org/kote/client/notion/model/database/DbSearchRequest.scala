package org.kote.client.notion.model.database

import io.circe.{Encoder, Json}
import org.kote.client.notion
import org.kote.client.notion.model.filter.DatabaseFilter
import org.kote.client.notion.model.list.PaginatedList.Cursor

case class DbSearchRequest(
    query: Option[String],
    cursor: Option[Cursor],
)

object DbSearchRequest {
  implicit val encoder: Encoder[DbSearchRequest] =
    Encoder.instance { source =>
      Json.obj(
        List(
          notion.optionEncode("query", source.query),
          notion.optionEncode("filter", Some(DatabaseFilter)),
          notion.optionEncode("start_cursor", source.cursor.map(_.value)),
          notion.optionEncode("page_size", source.cursor.map(_.pageSize)),
        ).flatMap {
          case Some(value) => List(value)
          case None        => List.empty
        }: _*,
      )
    }
}
