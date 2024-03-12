package org.kote.client.notion.model.parent

import io.circe.{Decoder, Encoder}
import org.kote.client.notion.model.database.DbId
import org.kote.client.notion.model.page.PageId

sealed trait Parent

object Parent {
  def db(id: DbId): DatabaseParent = DatabaseParent(id)

  def page(id: PageId): PageParent = PageParent(id)
}

final case class DatabaseParent(id: DbId) extends Parent

object DatabaseParent {
  implicit val databaseParentEncoder: Encoder[DatabaseParent] = Encoder[DbId].contramap(_.id)
  implicit val databaseParentDecoder: Decoder[DatabaseParent] = Decoder[DbId].map(DatabaseParent(_))

}

final case class PageParent(id: PageId) extends Parent

object PageParent {
  implicit val pageParentEncoder: Encoder[PageParent] = Encoder[PageId].contramap(_.id)
  implicit val pageParentDecoder: Decoder[PageParent] = Decoder[PageId].map(PageParent(_))
}
