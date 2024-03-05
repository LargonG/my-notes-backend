package org.kote.client.notion.model.database

import io.circe.{Decoder, Encoder}
import org.kote.client.notion.model.page.PageId
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserResponse

import java.util.UUID

/** Уменьшенная версия notion объекта Database
  * @param id
  *   UUID - идентификатор таблицы
  * @param createdBy
  *   Partial User - пользователь notion, которым была создана данная таблица
  * @param title
  *   list of rich text objects - заголовок базы данных
  * @param properties
  *   schema of properties "key" -> {value} - представление страниц в базе (какие значения они
  *   должны иметь в себе)
  * @param achieved
  *   удалена ли
  */
final case class DbResponse(
    id: DbId,
    createdBy: UserResponse,
    title: List[RichText],
    properties: Map[String, DbPropertyResponse],
    achieved: Boolean,
)
// Может потребоваться parent

object DbResponse {
  implicit val dbResponseDecoder: Decoder[DbResponse] =
    Decoder.forProduct5("id", "created_by", "title", "properties", "achieved")(DbResponse.apply)
}

/** Запрос создания новой базы данных
  * @param parent
  *   Notion page - куда будет прикреплён (родитель)
  * @param title
  *   list of rich text objects - заголовок базы данных
  * @param properties
  *   schema of properties object@"key" -> {value} - необходимые параметры для страниц в базе
  *   данных. Status не может быть задан (ограничение Notion API)
  */
final case class DbRequest(
    parent: PageId,
    title: Option[List[RichText]],
    properties: Map[String, DbPropertyRequest],
)

object DbRequest {
  implicit val dbRequestEncoder: Encoder[DbRequest] =
    Encoder.forProduct3("parent", "title", "properties") { source =>
      (source.parent, source.title, source.properties)
    }
}

final case class DbUpdateRequest(
    title: Option[List[RichText]],
    properties: Map[String, DbPropertyRequest],
)

final case class DbId(inner: UUID) extends AnyVal

object DbId {
  implicit val dbIdEncoder: Encoder[DbId] = Encoder.encodeUUID.contramap(_.inner)
  implicit val dbIdDecoder: Decoder[DbId] = Decoder.decodeUUID.map(DbId(_))
}
