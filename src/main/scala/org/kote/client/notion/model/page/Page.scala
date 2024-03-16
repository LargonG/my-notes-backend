package org.kote.client.notion.model.page

import io.circe.{Decoder, Encoder}
import org.kote.client.notion.model.block.BlockRequest
import org.kote.client.notion.model.parent.{DatabaseParent, Parent}
import org.kote.client.notion.model.user.UserResponse

import java.util.UUID

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

/** Запрос создания страницы прикреплённой к какой-то базе данных
  * @param parent
  *   база данных
  * @param properties
  *   свойства, должны сходится со свойствами базы данных
  * @param children
  *   контент
  */
case class PageRequest(
    parent: DatabaseParent,
    properties: Map[String, PagePropertyRequest],
    children: List[BlockRequest],
)

object PageRequest {
  implicit val pageRequestEncoder: Encoder[PageRequest] =
    Encoder.forProduct3("parent", "properties", "children") { source =>
      (source.parent, source.properties, source.children)
    }
}

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

final case class PageId(inner: UUID) extends AnyVal {
  override def toString: String = inner.toString
}

object PageId {
  implicit val pageIdEncoder: Encoder[PageId] = Encoder.encodeUUID.contramap(_.inner)
  implicit val pageIdDecoder: Decoder[PageId] = Decoder.decodeUUID.map(PageId(_))
}
