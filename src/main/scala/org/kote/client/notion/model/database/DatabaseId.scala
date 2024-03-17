package org.kote.client.notion.model.database

import io.circe.{Decoder, Encoder}

import java.util.UUID

final case class DatabaseId(inner: UUID) extends AnyVal {
  override def toString: String = inner.toString
}

object DatabaseId {
  implicit val dbIdEncoder: Encoder[DatabaseId] = Encoder.encodeUUID.contramap(_.inner)
  implicit val dbIdDecoder: Decoder[DatabaseId] = Decoder.decodeUUID.map(DatabaseId(_))
}
