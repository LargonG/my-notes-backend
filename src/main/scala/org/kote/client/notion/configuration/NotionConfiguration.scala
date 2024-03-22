package org.kote.client.notion.configuration

import scala.concurrent.duration.FiniteDuration

case class NotionConfiguration(
    token: String,
    url: String,
    version: String,
    timeout: FiniteDuration,
)
