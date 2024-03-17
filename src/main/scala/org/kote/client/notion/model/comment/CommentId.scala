package org.kote.client.notion.model.comment

import io.circe.{Decoder, Encoder}

import java.util.UUID

final case class CommentId(inner: UUID) extends AnyVal {
  override def toString: String = inner.toString
}

object CommentId {
  implicit val commentIdEncoder: Encoder[CommentId] = Encoder.encodeUUID.contramap(_.inner)
  implicit val commentIdDecoder: Decoder[CommentId] = Decoder.decodeUUID.map(CommentId(_))
}
