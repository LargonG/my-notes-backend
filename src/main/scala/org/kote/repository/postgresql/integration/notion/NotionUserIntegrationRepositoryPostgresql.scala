package org.kote.repository.postgresql.integration.notion

import cats.data.OptionT
import cats.effect.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import org.kote.client.notion.NotionUserId
import org.kote.domain.integration.notion.NotionUserIntegration
import org.kote.domain.user.User.UserId
import org.kote.repository.IntegrationRepository
import org.kote.repository.postgresql.QuillInstances

class NotionUserIntegrationRepositoryPostgresql[F[_]: MonadCancelThrow](implicit
    val tr: Transactor[F],
) extends IntegrationRepository[F, UserId, NotionUserId]
    with QuillInstances {
  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def set(key: UserId, value: NotionUserId): F[Long] =
    run {
      quote(
        query[NotionUserIntegration].insertValue(
          lift(NotionUserIntegration(key, value)),
        ),
      )
    }.transact(tr)

  override def getByKey(key: UserId): OptionT[F, NotionUserId] = OptionT(
    run {
      quote {
        query[NotionUserIntegration].filter(_.userId == lift(key))
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.notionUserId),
      ),
  )

  override def getByValue(value: NotionUserId): OptionT[F, UserId] = OptionT(
    run {
      quote {
        query[NotionUserIntegration].filter(_.notionUserId == lift(value))
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.userId),
      ),
  )

  override def delete(key: UserId): OptionT[F, NotionUserId] = OptionT(
    run {
      quote {
        query[NotionUserIntegration]
          .filter(_.userId == lift(key))
          .delete
          .returningMany(r => r)
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.notionUserId),
      ),
  )
}
