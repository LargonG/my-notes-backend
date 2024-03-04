package org.kote.client

import cats.ApplicativeThrow
import io.circe.Decoder
import org.kote.client.notion.configuration.NotionConfiguration
import sttp.client3.{Empty, RequestT, ResponseAs, basicRequest}
import sttp.client3.circe.asJsonAlways

package object notion {
  val v1 = "api/v1"

  def unwrap[F[_]: ApplicativeThrow, T: Decoder]: ResponseAs[F[T], Any] =
    asJsonAlways[T].map(ApplicativeThrow[F].fromEither(_))

  def basicRequestWithHeaders(implicit
      config: NotionConfiguration,
  ): RequestT[Empty, Either[String, String], Any] =
    basicRequest
      .header("Authorization", s"Bearer ${config.apiKey}")
      .header("Notion-Version", config.notionVersion)
}
