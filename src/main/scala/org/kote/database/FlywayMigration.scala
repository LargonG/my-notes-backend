package org.kote.database

import cats.effect.Sync
import cats.implicits.toFunctorOps
import org.flywaydb.core.Flyway
import org.kote.config.PostgresConfig

object FlywayMigration {

  private def loadFlyway(config: PostgresConfig): Flyway =
    Flyway
      .configure()
      .locations("db.migration")
      .cleanDisabled(false)
      .dataSource(config.url, config.user, config.password)
      .load()

  def migrate[F[_]: Sync](config: PostgresConfig): F[Unit] =
    Sync[F].delay(loadFlyway(config).migrate()).void

  def clean[F[_]: Sync](config: PostgresConfig): F[Unit] =
    Sync[F].delay(loadFlyway(config).clean()).void

}
