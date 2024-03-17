package org.kote.client.notion.model.database.request

import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import org.kote.client.notion.model.database.request.DatabasePropertyRequest.singleEncoder
import org.kote.client.notion.model.database.request.DatabaseSelectPropertyRequest.SelectOption

/** Запрос на создание свойства определённого типа */
sealed trait DatabasePropertyRequest

object DatabasePropertyRequest {
  def singleEncoder[T <: DatabasePropertyRequest](
      name: String,
      json: Json = Json.obj(),
  ): Encoder[T] =
    Encoder.instance { _ =>
      Json.obj(
        name -> json,
      )
    }

  implicit val dbPropertyRequestEncoder: Encoder[DatabasePropertyRequest] = Encoder.instance {
    case DatabaseFilesPropertyRequest$             => DatabaseFilesPropertyRequest$.asJson
    case DatabasePeoplePropertyRequest$            => DatabasePeoplePropertyRequest$.asJson
    case DatabaseRichTextPropertyRequest$          => DatabaseRichTextPropertyRequest$.asJson
    case DatabaseTitlePropertyRequest$             => DatabaseTitlePropertyRequest$.asJson
    case select @ DatabaseSelectPropertyRequest(_) => select.asJson
  }

  def files: DatabaseFilesPropertyRequest$.type =
    DatabaseFilesPropertyRequest$

  def people: DatabasePeoplePropertyRequest$.type =
    DatabasePeoplePropertyRequest$

  def richText: DatabaseRichTextPropertyRequest$.type =
    DatabaseRichTextPropertyRequest$

  def title: DatabaseRichTextPropertyRequest$.type =
    DatabaseRichTextPropertyRequest$

  def select(options: List[SelectOption]): DatabaseSelectPropertyRequest =
    DatabaseSelectPropertyRequest(options)

  def selectOption(name: String): DatabaseSelectPropertyRequest.SelectOption =
    SelectOption(name)
}

case object DatabaseFilesPropertyRequest$ extends DatabasePropertyRequest {
  implicit val dbFilesPropertyRequestEncoder: Encoder[DatabaseFilesPropertyRequest$.type] =
    singleEncoder("title")
}
case object DatabasePeoplePropertyRequest$ extends DatabasePropertyRequest {
  implicit val dbPeoplePropertyRequestEncoder: Encoder[DatabasePeoplePropertyRequest$.type] =
    singleEncoder("people")
}
case object DatabaseRichTextPropertyRequest$ extends DatabasePropertyRequest {
  implicit val dbRichTextPropertyRequest: Encoder[DatabaseRichTextPropertyRequest$.type] =
    singleEncoder("rich_text")
}
case object DatabaseTitlePropertyRequest$ extends DatabasePropertyRequest {
  implicit val dbTitlePropertyRequestEncoder: Encoder[DatabaseTitlePropertyRequest$.type] =
    singleEncoder("title")
}
final case class DatabaseSelectPropertyRequest(options: List[SelectOption])
    extends DatabasePropertyRequest

object DatabaseSelectPropertyRequest {
  final case class SelectOption(name: String)
  object SelectOption {
    implicit val selectOptionEncoder: Encoder[SelectOption] = deriveEncoder
  }

  implicit val dbSelectPropertyRequestEncoder: Encoder[DatabaseSelectPropertyRequest] =
    Encoder.instance { value =>
      Json.obj(
        "select" -> Json.obj(
          "options" -> value.options.asJson,
        ),
      )
    }

}
