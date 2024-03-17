package org.kote.client.notion.model.page.response

import io.circe.Decoder
import org.kote.client.notion.model.page.PageId
import org.kote.client.notion.model.parent.Parent
import org.kote.client.notion.model.user.UserResponse

/** Урезанная версия страницы Notion */
final case class PageResponse(
    id: PageId,
    createdBy: UserResponse,
    parent: Parent,
    archived: Boolean,
    properties: Map[String, PagePropertyResponse],
)

object PageResponse {
  implicit val pageResponseDecoder: Decoder[PageResponse] =
    Decoder.forProduct5("id", "created_by", "parent", "archived", "properties")(PageResponse.apply)
}
