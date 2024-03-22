package org.kote.client.notion.model.database.request

import io.circe.Encoder
import org.kote.client.notion.model.parent.PageParent
import org.kote.client.notion.model.text.RichText

/** Запрос создания новой базы данных
  *
  * @param parent
  *   Notion page - куда будет прикреплён (родитель)
  * @param title
  *   list of rich text objects - заголовок базы данных
  * @param properties
  *   schema of properties object@"key" -> {value} - необходимые параметры для страниц в базе
  *   данных. Status не может быть задан (ограничение Notion API)
  */
final case class DatabaseRequest(
    parent: PageParent,
    title: Option[List[RichText]],
    properties: Map[String, DatabasePropertyRequest],
)
object DatabaseRequest {
  implicit val dbRequestEncoder: Encoder[DatabaseRequest] =
    Encoder.forProduct3("parent", "title", "properties") { source =>
      (source.parent, source.title, source.properties)
    }
}
