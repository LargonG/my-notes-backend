package org.kote.client.notion.model.database

import org.kote.client.notion.model.database.DbSelectPropertyResponse.SelectOption
import org.kote.client.notion.model.database.DbStatusPropertyResponse.StatusOption

/** Общие поля для всех типов свойств базы данных
  * @param id
  *   string - какая-то случайная строка, не UUID
  * @param name
  *   string - как это выглядит в Notion (его название)
  * @param valueType
  *   enum: "files", "people", "rich_text", "status", "title", "select"
  * @param value
  *   зависит от того, какой задан [[valueType]]
  */
final case class DbPropertyResponse(
    id: String,
    name: String,
    valueType: String,
    value: DbPropertyResponseValue,
)

sealed trait DbPropertyResponseValue

/** Колонка хранения файлов */
final case class DbFilesPropertyResponse() extends DbPropertyResponseValue

/** Колонка названия страницы */
final case class DbTitlePropertyResponse() extends DbPropertyResponseValue

/** Урезанная колонка статуса. Её нельзя менять через Notion API, только смотреть
  * @param options
  *   допустимые значения status
  */
final case class DbStatusPropertyResponse(
    options: List[StatusOption],
) extends DbPropertyResponseValue

object DbStatusPropertyResponse {

  /** Варианты выбора статуса
    * @param id
    *   ПОЧТИ всегда UUID
    * @param name
    *   отображается в UI
    */
  final case class StatusOption(id: String, name: String)
}

/** Колонка упоминания людей */
final case class DbPeoplePropertyResponse() extends DbPropertyResponseValue

/** Колонка текста (просто текста) */
final case class DbRichTextPropertyResponse() extends DbPropertyResponseValue

final case class DbSelectPropertyResponse(
    options: List[SelectOption],
)

object DbSelectPropertyResponse {
  final case class SelectOption(id: String, name: String)
}
