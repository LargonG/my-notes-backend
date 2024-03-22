package org.kote.client.notion.model.database.response

import io.circe.Decoder
import org.kote.client.notion.model.database.DatabaseId
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserResponse

/** Уменьшенная версия notion объекта Database
  *
  * @param id
  *   UUID - идентификатор таблицы
  * @param createdBy
  *   Partial User - пользователь notion, которым была создана данная таблица
  * @param title
  *   list of rich text objects - заголовок базы данных
  * @param properties
  *   schema of properties "key" -> {value} - представление страниц в базе (какие значения они
  *   должны иметь в себе)
  * @param archived
  *   удалена ли
  */
final case class DatabaseResponse(
    id: DatabaseId,
    createdBy: UserResponse,
    title: List[RichText],
    properties: Map[String, DatabasePropertyResponse],
    archived: Boolean,
)
object DatabaseResponse {
  implicit val dbResponseDecoder: Decoder[DatabaseResponse] =
    Decoder.forProduct5("id", "created_by", "title", "properties", "archived")(
      DatabaseResponse.apply,
    )
}
