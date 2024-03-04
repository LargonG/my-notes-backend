package org.kote.client.notion.model.page

import org.kote.client.notion.model.block.BlockRequest
import org.kote.client.notion.model.parent.DatabaseParent
import org.kote.client.notion.model.user.UserResponse

import java.util.UUID

/** Урезанная версия страницы Notion */
final case class PageResponse(
    id: PageId,
    createdBy: UserResponse,
    achieved: Boolean,
    properties: Map[String, PagePropertyResponse],
    parent: DatabaseParent,
)

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

final case class PageId(inner: UUID) extends AnyVal
