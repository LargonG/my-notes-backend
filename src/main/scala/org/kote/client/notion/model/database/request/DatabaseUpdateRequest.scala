package org.kote.client.notion.model.database.request

import io.circe.Encoder
import org.kote.client.notion.model.text.RichText

final case class DatabaseUpdateRequest(
    title: Option[List[RichText]],
    properties: Map[String, DatabasePropertyRequest],
)

object DatabaseUpdateRequest {
  implicit val dbUpdateRequestEncoder: Encoder[DatabaseUpdateRequest] =
    Encoder.forProduct2("title", "properties") { source =>
      (source.title, source.properties)
    }
}
