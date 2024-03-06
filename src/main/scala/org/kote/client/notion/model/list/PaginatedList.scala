package org.kote.client.notion.model.list

import io.circe.{Decoder, Encoder}

/** Иногда notion api возвращает объект типа "list", представляющий из себя фрагмент. Внутри этого
  * объекта хранится информацию о том, как нам достать следующий фрагмент
  * @param results
  *   интересующие объекты
  * @param nextCursor
  *   указатель на следующий фрагмент, обычно устанавливается в QUERY PARAMS, либо в BODY PARAMS
  * @param hasMore
  *   есть ли ещё фрагменты после данного
  * @tparam T
  *   может быть любой тип Response из notion.model
  */
case class PaginatedList[T](
    results: List[T],
    nextCursor: Option[String],
    hasMore: Boolean,
)

object PaginatedList {
  final case class Cursor(value: String, pageSize: Int = 32)
  object Cursor {
    implicit val cursorEncoder: Encoder[Cursor] = Encoder.forProduct2("start_cursor", "page_size") {
      cursor => (cursor.value, cursor.pageSize)
    }
  }

  implicit def paginatedListDecoder[T: Decoder]: Decoder[PaginatedList[T]] =
    Decoder.forProduct3("results", "next_cursor", "has_more") {
      (results: List[T], nextCursor: Option[String], hasMore: Boolean) =>
        PaginatedList(results, nextCursor, hasMore)
    }
}
