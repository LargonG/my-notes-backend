package org.kote.client.notion.model.block

import io.circe.{Decoder, Encoder}

import java.util.UUID

final case class BlockId(inner: UUID) extends AnyVal {
  override def toString: String = inner.toString
}

object BlockId {
  implicit val blockIdEncoder: Encoder[BlockId] = Encoder.encodeUUID.contramap(_.inner)
  implicit val blockIdDecoder: Decoder[BlockId] = Decoder.decodeUUID.map(BlockId(_))
}
