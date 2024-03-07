package org.kote.client.notion

import cats.data.OptionT
import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.page._
import org.kote.client.notion.model.property.PropertyItem
import sttp.client3.{SttpBackend, UriContext}
import sttp.client3.circe._

trait NotionPageClient[F[_]] {
  def create(request: PageRequest): F[PageResponse]

  def get(id: PageId): OptionT[F, PageResponse]

  def getProperty(pageId: PageId, propertyId: String): OptionT[F, PropertyItem]

  def updateProperty(
      pageId: PageId,
      request: PageUpdateRequest,
  ): OptionT[F, PageResponse]

  def achieve(pageId: PageId): OptionT[F, PageResponse]
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

  // todo: оно пока что возвращает paginated list, а мы с ним в проекте не хотим работать,
  //  если будут возникать проблемы -- поправить
  override def getProperty(pageId: PageId, propertyId: String): OptionT[F, PropertyItem] =
    OptionT(
      basicRequestWithHeaders
        .get(uri"$pages/$pageId/properties/$propertyId")
        .response(unwrap[F, PropertyItem])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def updateProperty(
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

  override def achieve(pageId: PageId): OptionT[F, PageResponse] =
    updateProperty(pageId, PageUpdateRequest(Map.empty, achieved = true))
}
