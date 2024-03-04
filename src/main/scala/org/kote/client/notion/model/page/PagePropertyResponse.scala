package org.kote.client.notion.model.page

import org.kote.client.notion.model.file.File
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserResponse

/** Общие поля для всех типов свойств страницы в базе данных
  * @param id
  *   string - случайная строка
  * @param valueType
  *   enum: "files", "people", "rich_text", "status", "title", "select"
  * @param value
  *   значение определённого типа
  */
final case class PagePropertyResponse(
    id: String,
    valueType: String,
    value: PagePropertyResponseValue,
)

// Можно было, кстати, сделать enum, ну да ладно

sealed trait PagePropertyResponseValue

final case class PageFilesPropertyResponse(files: List[File]) extends PagePropertyResponseValue

final case class PagePeoplePropertyResponse(people: List[UserResponse])
    extends PagePropertyResponseValue

final case class PageRichTextPropertyResponse(text: List[RichText])
    extends PagePropertyResponseValue

final case class PageStatusPropertyResponse(id: String, name: String)
    extends PagePropertyResponseValue

final case class PageTitlePropertyResponse(text: List[RichText]) extends PagePropertyResponseValue

final case class PageSelectPropertyResponse(id: String, name: String)
    extends PagePropertyResponseValue
