package org.kote.client.notion.model.parent

import org.kote.client.notion.model.database.DbId
import org.kote.client.notion.model.page.PageId

sealed trait Parent

final case class DatabaseParent(id: DbId) extends Parent

final case class PageParent(id: PageId) extends Parent

case object WorkspaceParent extends Parent
