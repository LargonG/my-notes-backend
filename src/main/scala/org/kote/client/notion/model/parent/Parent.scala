package org.kote.client.notion.model.parent

import cats.implicits.toFunctorOps
import io.circe.{Decoder, Encoder}
import org.kote.client.notion.model.database.DbId
import org.kote.client.notion.model.page.PageId

sealed trait Parent

object Parent {
  def db(id: DbId): DatabaseParent = DatabaseParent(id)

  def page(id: PageId): PageParent = PageParent(id)

  def workspace: WorkspaceParent = WorkspaceParent(true)

  implicit val parentDecoder: Decoder[Parent] =
    List[Decoder[Parent]](
      Decoder[DatabaseParent].widen,
      Decoder[PageParent].widen,
      Decoder[WorkspaceParent].widen,
    ).reduceLeft(_ or _)
}

final case class DatabaseParent(id: DbId) extends Parent

object DatabaseParent {
  implicit val databaseParentEncoder: Encoder[DatabaseParent] =
    Encoder.forProduct2("type", "database_id")(source => ("database_id", source.id))
  implicit val databaseParentDecoder: Decoder[DatabaseParent] =
    Decoder.forProduct2("type", "database_id")((_: String, id) => DatabaseParent(id))

}

final case class PageParent(id: PageId) extends Parent

object PageParent {
  implicit val pageParentEncoder: Encoder[PageParent] =
    Encoder.forProduct2("type", "page_id")(source => ("page_id", source.id))
  implicit val pageParentDecoder: Decoder[PageParent] =
    Decoder.forProduct2("type", "page_id")((_: String, id) => PageParent(id))
}

final case class WorkspaceParent(workspace: Boolean) extends Parent

object WorkspaceParent {
  implicit val workspaceParentEncoder: Encoder[WorkspaceParent] =
    Encoder.forProduct2("type", "workspace")(source => ("workspace", source.workspace))

  implicit val workspaceParentDecoder: Decoder[WorkspaceParent] =
    Decoder.forProduct2("type", "workspace")((_: String, workspace) => WorkspaceParent(workspace))
}
