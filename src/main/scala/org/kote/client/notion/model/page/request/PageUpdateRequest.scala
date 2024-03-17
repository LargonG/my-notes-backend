package org.kote.client.notion.model.page.request

import io.circe.Encoder

case class PageUpdateRequest(
    properties: Map[String, PagePropertyRequest],
    archived: Boolean = false,
)

object PageUpdateRequest {
  implicit val pageUpdateRequestEncoder: Encoder[PageUpdateRequest] = {
    import io.circe.generic.semiauto.deriveEncoder
    deriveEncoder
  }
}
