package org.kote.repository.postgresql.integration.notion

import cats.data.OptionT
import cats.effect.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import org.kote.client.notion.NotionPageId
import org.kote.domain.integration.notion.NotionMainPageIntegration
import org.kote.domain.user.User.UserId
import org.kote.repository.IntegrationRepository
import org.kote.repository.postgresql.QuillInstances

class NotionMainPageIntegrationRepositoryPostgresql[F[_]: MonadCancelThrow](implicit
    val tr: Transactor[F],
) extends IntegrationRepository[F, UserId, NotionPageId]
    with QuillInstances {

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def set(key: UserId, value: NotionPageId): F[Long] =
    run {
      quote(
        query[NotionMainPageIntegration].insertValue(
          lift(NotionMainPageIntegration(key, value)),
        ),
      )
    }.transact(tr)

  override def getByKey(key: UserId): OptionT[F, NotionPageId] = OptionT(
    run {
      quote {
        query[NotionMainPageIntegration].filter(_.userId == lift(key))
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.mainPage),
      ),
  )

  override def getByValue(value: NotionPageId): OptionT[F, UserId] = OptionT(
    run {
      quote {
        query[NotionMainPageIntegration].filter(_.mainPage == lift(value))
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.userId),
      ),
  )

  override def delete(key: UserId): OptionT[F, NotionPageId] = OptionT(
    run {
      quote {
        query[NotionMainPageIntegration]
          .filter(_.userId == lift(key))
          .delete
          .returningMany(r => r)
      }
    }.transact(tr)
      .map(
        _.headOption.map(_.mainPage),
      ),
  )
}
