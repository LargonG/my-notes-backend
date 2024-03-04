package org.kote.client.notion.model.list

import io.circe.Decoder
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

case class PaginatedList[T](
    hasMore: Boolean,
    nextCursor: Option[String],
    results: List[T],
)

object PaginatedList {
  implicit def paginatedListDecoder[T: Decoder]: Decoder[PaginatedList[T]] =
    Decoder.forProduct3("has_more", "next_cursor", "results") {
      (hasMore: Boolean, nextCursor: String, results: List[T]) =>
        PaginatedList(hasMore, Option(nextCursor), results)
    }
}
