package org.kote.repository.postgresql.integration.notion

import cats.data.OptionT
import cats.effect.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import org.kote.client.notion.NotionDatabaseId
import org.kote.domain.board.Board.BoardId
import org.kote.domain.integration.notion.NotionDatabaseIntegration
import org.kote.repository.IntegrationRepository
import org.kote.repository.postgresql.QuillInstances

class NotionDatabaseIntegrationRepositoryPostgresql[F[_]: MonadCancelThrow](implicit
    val tr: Transactor[F],
) extends IntegrationRepository[F, BoardId, NotionDatabaseId]
    with QuillInstances {
  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def set(key: BoardId, value: NotionDatabaseId): F[Long] =
    run {
      quote(
        query[NotionDatabaseIntegration].insertValue(
          lift(NotionDatabaseIntegration(key, value)),
        ),
      )
    }.transact(tr)

  override def getByKey(key: BoardId): OptionT[F, NotionDatabaseId] = OptionT(
    run {
      quote {
        query[NotionDatabaseIntegration].filter(_.boardId == lift(key))
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.notionDatabaseId),
      ),
  )

  override def getByValue(value: NotionDatabaseId): OptionT[F, BoardId] = OptionT(
    run {
      quote {
        query[NotionDatabaseIntegration].filter(_.notionDatabaseId == lift(value))
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.boardId),
      ),
  )

  override def delete(key: BoardId): OptionT[F, NotionDatabaseId] = OptionT(
    run {
      quote {
        query[NotionDatabaseIntegration]
          .filter(_.boardId == lift(key))
          .delete
          .returningMany(r => r)
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.notionDatabaseId),
      ),
  )
}
