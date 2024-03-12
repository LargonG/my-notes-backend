package org.kote.client.notion.model.database

import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import org.kote.client.notion.model.database.DbPropertyRequest.singleEncoder
import org.kote.client.notion.model.database.DbSelectPropertyRequest.SelectOption

/** Запрос на создание свойства определённого типа */
sealed trait DbPropertyRequest

object DbPropertyRequest {
  def singleEncoder[T <: DbPropertyRequest](name: String, json: Json = Json.obj()): Encoder[T] =
    Encoder.instance { _ =>
      Json.obj(
        name -> json,
      )
    }

  implicit val dbPropertyRequestEncoder: Encoder[DbPropertyRequest] = Encoder.instance {
    case DbFilesPropertyRequest              => DbFilesPropertyRequest.asJson
    case DbPeoplePropertyRequest             => DbPeoplePropertyRequest.asJson
    case DbRichTextPropertyRequest           => DbRichTextPropertyRequest.asJson
    case DbTitlePropertyRequest              => DbTitlePropertyRequest.asJson
    case select @ DbSelectPropertyRequest(_) => select.asJson
  }

  def files: DbFilesPropertyRequest.type =
    DbFilesPropertyRequest

  def people: DbPeoplePropertyRequest.type =
    DbPeoplePropertyRequest

  def richText: DbRichTextPropertyRequest.type =
    DbRichTextPropertyRequest

  def title: DbRichTextPropertyRequest.type =
    DbRichTextPropertyRequest

  def select(options: List[SelectOption]): DbSelectPropertyRequest =
    DbSelectPropertyRequest(options)

  def selectOption(name: String): DbSelectPropertyRequest.SelectOption =
    SelectOption(name)
}

case object DbFilesPropertyRequest extends DbPropertyRequest {
  implicit val dbFilesPropertyRequestEncoder: Encoder[DbFilesPropertyRequest.type] =
    singleEncoder("title")
}
case object DbPeoplePropertyRequest extends DbPropertyRequest {
  implicit val dbPeoplePropertyRequestEncoder: Encoder[DbPeoplePropertyRequest.type] =
    singleEncoder("people")
}
case object DbRichTextPropertyRequest extends DbPropertyRequest {
  implicit val dbRichTextPropertyRequest: Encoder[DbRichTextPropertyRequest.type] =
    singleEncoder("rich_text")
}
case object DbTitlePropertyRequest extends DbPropertyRequest {
  implicit val dbTitlePropertyRequestEncoder: Encoder[DbTitlePropertyRequest.type] =
    singleEncoder("title")
}
final case class DbSelectPropertyRequest(options: List[SelectOption]) extends DbPropertyRequest

object DbSelectPropertyRequest {
  final case class SelectOption(name: String)
  object SelectOption {
    implicit val selectOptionEncoder: Encoder[SelectOption] = deriveEncoder
  }

  implicit val dbSelectPropertyRequestEncoder: Encoder[DbSelectPropertyRequest] =
    Encoder.instance { value =>
      Json.obj(
        "select" -> Json.obj(
          "options" -> value.options.asJson,
        ),
      )
    }

}
