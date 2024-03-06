package org.kote.client.notion.model.database

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.kote.client.notion.model.database.DbSelectPropertyResponse.SelectOption
import org.kote.client.notion.model.database.DbStatusPropertyResponse.StatusOption
import org.kote.client.notion.model.property.{
  FilesType,
  PeopleType,
  PropertyType,
  RichTextType,
  SelectType,
  StatusType,
  TitleType,
}

/** Общие поля для всех типов свойств базы данных
  * @param id
  *   string - какая-то случайная строка, не UUID
  * @param name
  *   string - как это выглядит в Notion (его название)
  * @param value
  *   значение свойства (в json зависит от того, как задан "type")
  */
final case class DbPropertyResponse(
    id: String,
    name: String,
    value: DbPropertyValueResponse,
)

object DbPropertyResponse {
  implicit val dbPropertyResponseDecoder: Decoder[DbPropertyResponse] =
    Decoder.instance { cursor =>
      for {
        id <- cursor.get[String]("id")
        name <- cursor.get[String]("name")
        valueType <- cursor.get[PropertyType]("type")
        value <- valueType match {
          case FilesType    => cursor.get[DbFilesPropertyResponse.type](valueType)
          case PeopleType   => cursor.get[DbPeoplePropertyResponse.type](valueType)
          case RichTextType => cursor.get[DbRichTextPropertyResponse.type](valueType)
          case StatusType   => cursor.get[DbStatusPropertyResponse](valueType)
          case TitleType    => cursor.get[DbTitlePropertyResponse.type](valueType)
          case SelectType   => cursor.get[DbSelectPropertyResponse](valueType)
        }
      } yield DbPropertyResponse(id, name, value)
    }
}

sealed trait DbPropertyValueResponse

/** Колонка хранения файлов */
case object DbFilesPropertyResponse extends DbPropertyValueResponse {
  implicit val dbFilesPropertyResponseDecoder: Decoder[DbFilesPropertyResponse.type] = deriveDecoder
}

/** Колонка названия страницы */
case object DbTitlePropertyResponse extends DbPropertyValueResponse {
  implicit val dbTitlePropertyResponseDecoder: Decoder[DbTitlePropertyResponse.type] = deriveDecoder
}

/** Урезанная колонка статуса. Её нельзя менять через Notion API, только смотреть
  * @param options
  *   допустимые значения status
  */
final case class DbStatusPropertyResponse(
    options: List[StatusOption],
) extends DbPropertyValueResponse

object DbStatusPropertyResponse {

  /** Варианты выбора статуса
    * @param id
    *   ПОЧТИ всегда UUID
    * @param name
    *   отображается в UI
    */
  final case class StatusOption(id: String, name: String)
  object StatusOption {
    implicit val statusOptionDecoder: Decoder[StatusOption] = deriveDecoder
  }

  implicit val dbStatusPropertyResponseDecoder: Decoder[DbStatusPropertyResponse] = deriveDecoder
}

/** Колонка упоминания людей */
case object DbPeoplePropertyResponse extends DbPropertyValueResponse {
  implicit val dbPeoplePropertyResponseDecoder: Decoder[DbPeoplePropertyResponse.type] =
    deriveDecoder
}

/** Колонка текста (просто текста) */
case object DbRichTextPropertyResponse extends DbPropertyValueResponse {
  implicit val dbRichTextPropertyResponseDecoder: Decoder[DbRichTextPropertyResponse.type] =
    deriveDecoder
}

final case class DbSelectPropertyResponse(
    options: List[SelectOption],
) extends DbPropertyValueResponse

object DbSelectPropertyResponse {
  final case class SelectOption(id: String, name: String)
  object SelectOption {
    implicit val dbSelectOptionDecoder: Decoder[SelectOption] = deriveDecoder
  }

  implicit val dbSelectPropertyResponse: Decoder[DbSelectPropertyResponse] = deriveDecoder
}
