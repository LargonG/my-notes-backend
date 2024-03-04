package org.kote.client.notion

import cats.data.OptionT
import org.kote.client.notion.model.database.{DbId, DbRequest, DbResponse}
import org.kote.client.notion.model.page.PageResponse

trait NotionDatabaseClient[F[_]] {
  def create(request: DbRequest): F[DbResponse]

  def get(id: DbId): OptionT[F, DbResponse]

  def list(id: DbId): OptionT[F, List[PageResponse]]
}
