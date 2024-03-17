package org.kote.client.notion.model.page.request

import io.circe.Encoder
import org.kote.client.notion.model.block.request.BlockRequest
import org.kote.client.notion.model.parent.DatabaseParent

/** Запрос создания страницы прикреплённой к какой-то базе данных
  *
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
