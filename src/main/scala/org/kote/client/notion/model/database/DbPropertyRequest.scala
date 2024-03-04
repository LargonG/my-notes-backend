package org.kote.client.notion.model.database

import org.kote.client.notion.model.database.DbPropertySelectRequest.SelectOption

/** Запрос на создание свойства определённого типа */
sealed trait DbPropertyRequest

case object DbPropertyFilesRequest extends DbPropertyRequest
case object DbPropertyPeopleRequest extends DbPropertyRequest
case object DbPropertyRichTextRequest extends DbPropertyRequest
case object DbPropertyTitleRequest extends DbPropertyRequest
final case class DbPropertySelectRequest(options: List[SelectOption]) extends DbPropertyRequest

object DbPropertySelectRequest {
  final case class SelectOption(name: String)
}
