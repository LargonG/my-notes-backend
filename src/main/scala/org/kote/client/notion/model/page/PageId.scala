package org.kote.client.notion.model.page

import io.circe.{Decoder, Encoder}

import java.util.UUID

final case class PageId(inner: UUID) extends AnyVal {
  override def toString: String = inner.toString
}

object PageId {
  implicit val pageIdEncoder: Encoder[PageId] = Encoder.encodeUUID.contramap(_.inner)
  implicit val pageIdDecoder: Decoder[PageId] = Decoder.decodeUUID.map(PageId(_))
}
