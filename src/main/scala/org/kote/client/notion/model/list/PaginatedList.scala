package org.kote.client.notion.model.list

import io.circe.{Decoder, Encoder}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

case class PaginatedList[T](
    hasMore: Boolean,
    nextCursor: Option[String],
    results: List[T],
)

object PaginatedList {
  final case class Cursor(value: String, pageSize: Int = 32)
  object Cursor {
    implicit val cursorEncoder: Encoder[Cursor] = Encoder.forProduct2("start_cursor", "page_size") {
      cursor => (cursor.value, cursor.pageSize)
    }
  }

  implicit def paginatedListDecoder[T: Decoder]: Decoder[PaginatedList[T]] =
    Decoder.forProduct3("has_more", "next_cursor", "results") {
      (hasMore: Boolean, nextCursor: String, results: List[T]) =>
        PaginatedList(hasMore, Option(nextCursor), results)
    }
}
