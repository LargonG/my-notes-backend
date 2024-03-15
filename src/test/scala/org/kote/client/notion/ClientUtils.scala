package org.kote.client.notion

import cats.effect.IO
import org.asynchttpclient.DefaultAsyncHttpClient
import org.kote.client.notion.configuration.NotionConfiguration
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

import scala.concurrent.duration.DurationInt

trait ClientUtils {
  val backend: SttpBackend[IO, Any] =
    AsyncHttpClientCatsBackend.usingClient[IO](new DefaultAsyncHttpClient())

  val notionConfig: NotionConfiguration = NotionConfiguration(
    "secret_TRstxQadmcWlZbRwXUlr2WZdTujLi2F1AGb1CLEFTyw",
    "https://api.notion.com",
    "2022-06-28",
    10.seconds,
  )
}
