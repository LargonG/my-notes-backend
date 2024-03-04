package org.kote.client.notion.configuration

import scala.concurrent.duration.FiniteDuration

case class NotionConfiguration(
    apiKey: String,
    notionVersion: String,
    url: String,
    timeout: FiniteDuration,
) {}
