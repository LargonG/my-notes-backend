package org.kote.client.notion

import cats.data.OptionT
import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.page._
import org.kote.client.notion.model.property.PropertyItem
import sttp.client3.circe._
import sttp.client3.{SttpBackend, UriContext}

trait NotionPageClient[F[_]] {
  def create(request: NotionPageCreateRequest): F[NotionPageResponse]

  def get(id: NotionPageId): OptionT[F, NotionPageResponse]

  def getPropertyItem(
      pageId: NotionPageId,
      propertyId: String,
  ): OptionT[F, NotionPagePropertyItemResponse]

  def updateProperties(
      pageId: NotionPageId,
      request: NotionPagePropertiesUpdateRequest,
  ): OptionT[F, NotionPageResponse]

  def achieve(pageId: NotionPageId): OptionT[F, NotionPageResponse]
}

final class NotionPageHttpClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    implicit val config: NotionConfiguration,
) extends NotionPageClient[F] {
  private val pages = s"${config.url}/$v1/pages"

  override def create(request: PageRequest): F[PageResponse] =
    basicRequestWithHeaders
      .post(uri"$pages")
      .body(request)
      .response(unwrap[F, PageResponse])
      .readTimeout(config.timeout)
      .send(sttpBackend)
      .flatMap(_.body)

  override def get(id: PageId): OptionT[F, PageResponse] =
    OptionT(
      basicRequestWithHeaders
        .get(uri"$pages/$id")
        .response(unwrap[F, PageResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def getPropertyItem(pageId: PageId, propertyId: String): OptionT[F, PropertyItem] =
    OptionT(
      basicRequestWithHeaders
        .get(uri"$pages/$pageId/properties/$propertyId")
        .response(unwrap[F, PropertyItem])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def achieve(pageId: PageId): OptionT[F, PageResponse] =
    updateProperties(pageId, PageUpdateRequest(Map.empty, achieved = true))

  override def updateProperties(
      pageId: PageId,
      request: PageUpdateRequest,
  ): OptionT[F, PageResponse] =
    OptionT(
      basicRequestWithHeaders
        .patch(uri"$pages/$pageId")
        .body(request)
        .response(unwrap[F, PageResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )
}
