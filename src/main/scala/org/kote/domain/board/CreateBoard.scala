package org.kote.domain.board

import org.kote.client.notion.NotionDatabaseCreateRequest
import org.kote.client.notion.model.database.{DbPropertyRequest, DbRequest}
import org.kote.client.notion.model.page.PageId
import org.kote.client.notion.model.text.RichText
import org.kote.common.tethys.TethysInstances
import org.kote.domain.user.User.UserId
import org.kote.service.notion.v1.PropertiesNames
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class CreateBoard(
    title: String,
    createdBy: UserId,
)

object CreateBoard extends TethysInstances {
  @nowarn
  implicit val createBoardReader: JsonReader[CreateBoard] = jsonReader

  @nowarn
  implicit val createBoardWriter: JsonWriter[CreateBoard] = jsonWriter

  implicit val createBoardSchema: Schema[CreateBoard] =
    Schema.derived.description("Запрос создания доски")

  def toNotionRequest(request: CreateBoard, mainPage: PageId): NotionDatabaseCreateRequest =
    DbRequest(
      mainPage,
      Some(List(RichText.text(request.title))),
      Map(
        PropertiesNames.titlePropertyName -> DbPropertyRequest.title,
        PropertiesNames.filesPropertyName -> DbPropertyRequest.files,
        PropertiesNames.assignsPropertyName -> DbPropertyRequest.people,
        PropertiesNames.groupPropertyName -> DbPropertyRequest.richText,
        PropertiesNames.statusPropertyName -> DbPropertyRequest.select(List()),
      ),
    )
}
