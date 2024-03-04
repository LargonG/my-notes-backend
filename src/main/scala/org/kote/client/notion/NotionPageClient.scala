package org.kote.client.notion

import cats.data.OptionT
import org.kote.client.notion.model.page._

trait NotionPageClient[F[_]] {
  def create(request: PageRequest): F[PageResponse]

  def get(id: PageId): OptionT[F, PageResponse]

  def getContent: OptionT[F, _]

  def getProperty(pageId: PageId, propertyId: String): OptionT[F, PagePropertyResponse]

  def updateProperty(
      pageId: PageId,
      request: Map[String, PagePropertyRequest],
  ): OptionT[F, PageResponse]

  def achieve(pageId: PageId): OptionT[F, PageResponse]
}
