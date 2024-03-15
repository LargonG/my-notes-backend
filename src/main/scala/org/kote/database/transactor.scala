package org.kote.database

import cats.effect.{Async, Resource}
import cats.implicits.catsSyntaxOptionId
import doobie.ExecutionContexts
import doobie.hikari.{Config, HikariTransactor}
import org.kote.config.PostgresConfig

object transactor {
  def makeTransactor[F[_] : Async](conf: PostgresConfig): Resource[F, HikariTransactor[F]] = {

    val hikariConfig = Config(
      jdbcUrl = conf.url.some,
      username = conf.user.some,
      password = conf.password.some,
      maximumPoolSize = conf.poolSize.some,
      driverClassName = "org.postgresql.Driver".some
    )

    for {
      ce <- ExecutionContexts.fixedThreadPool[F](conf.poolSize)
      xa <- HikariTransactor.fromConfig[F](hikariConfig, ce)
    } yield xa
  }
}

