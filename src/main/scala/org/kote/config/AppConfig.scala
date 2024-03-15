package org.kote.config

import org.kote.client.notion.configuration.NotionConfiguration

import scala.concurrent.duration.DurationInt

case class PostgresConfig(url: String, user: String, password: String, poolSize: Int)

case class HttpServer(port: Int)

case class NotionConfig(token: String, url: String, version: String, timeout: Int) {
  def toNotionClientConfiguration: NotionConfiguration =
    NotionConfiguration(token, url, version, timeout.milliseconds)
}

case class AppConfig(database: PostgresConfig, http: HttpServer, notion: NotionConfig)
