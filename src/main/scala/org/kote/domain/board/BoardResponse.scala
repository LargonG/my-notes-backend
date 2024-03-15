package org.kote.domain.board

import cats.Monad
import cats.data.OptionT
import org.kote.client.notion.{NotionDatabaseId, NotionDatabaseResponse, NotionUserId}
import org.kote.common.tethys.TethysInstances
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User.UserId
import org.kote.repository.IntegrationRepository
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import scala.annotation.nowarn

final case class BoardResponse(
    id: BoardId,
    title: String,
    owner: UserId,
)

object BoardResponse extends TethysInstances {
  @nowarn
  implicit val boardResponseReader: JsonReader[BoardResponse] = jsonReader

  @nowarn
  implicit val boardResponseWriter: JsonWriter[BoardResponse] = jsonWriter

  implicit val boardResponseSchema: Schema[BoardResponse] =
    Schema.derived.description("Доска")

  /** @param response
    * @param properties
    *   специально сформированные id для property, который считается за group
    *
    * Имеют следующий вид:{{{s"property.name-property.id-property.value"}}}
    * @param boardToDatabaseIds
    * @param userToUserIds
    * @tparam F
    * @return
    */
  def fromNotionResponse[F[_]: Monad](
      value: Option[NotionDatabaseResponse],
  )(
      boardToDatabaseIds: IntegrationRepository[F, BoardId, NotionDatabaseId],
      userToUserIds: IntegrationRepository[F, UserId, NotionUserId],
  ): OptionT[F, BoardResponse] =
    for {
      response <- OptionT.fromOption(value)
      id <- boardToDatabaseIds.getByValue(response.id)
      userId <- userToUserIds.getByValue(response.createdBy.id)
    } yield BoardResponse(
      id,
      response.title.mkString,
      userId,
    )
}
