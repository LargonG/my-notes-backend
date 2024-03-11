package org.kote.client.notion

import cats.data.OptionT
import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.block.{BlockId, BlockRequest, BlockResponse}
import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.list.PaginatedList.Cursor
import org.kote.client.notion.model.page.PageId
import org.kote.client.notion.utils.Query.ToQuery
import sttp.client3.circe._
import sttp.client3.{SttpBackend, UriContext}

/** Клиент обработки блоков: создание/удаление/получение/обновление.
  *
  * Придерживается соглашения, описанного в [[org.kote.client.notion]]
  *
  * ==Соглашение о возвращаемых значениях==
  *
  * Все классы в данном пакете придерживаются следующего соглашения:
  *
  * Все методы придерживаются следующей структуры (может быть несколько аргументов):
  * {{{
  *   def methodName(request: Request): OptionT[F, Response]
  * }}}
  * ===returns:===
  *
  * None - на сервере произошла ошибка.
  *
  * Some - ответ
  *
  * F - кидает ошибку, если запрос не смог выполнится из-за неправильно введённых в него данных,
  * проблемы с доступом или такого ресурса больше не существует.
  */
trait NotionBlockClient[F[_]] {

  /** Добавляет новых детей к странице. Дети - блоки.
    * @param pageId
    *   страницы
    * @param children
    *   запрос-список запросов добавления потомков-блоков
    * @return
    *   Неполный список всех детей первого уровня страницы. Подробнее см. [[NotionBlockClient]]
    */
  def append(
      pageId: NotionPageId,
      children: List[NotionBlockRequest],
  ): OptionT[F, PaginatedList[NotionBlockResponse]]

  /** Делает запрос notion на получение информации о блоке, его содержимом.
    * @param id
    *   блока
    * @return
    *   Вся информация о блоке. Подробнее см. [[NotionBlockClient]]
    */
  def get(id: NotionBlockId): OptionT[F, NotionBlockResponse]

  /** Весь поверхностный контент страницы - дети-блоки первого поколения от этой страницы.
    * @param id
    *   страницы
    * @return
    *   Весь контент первого уровня страницы. Подробнее см. [[NotionBlockClient]]
    */
  def getContent(id: NotionPageId): OptionT[F, List[NotionBlockResponse]]

  /** Обновляет контент существующего блока по его id.
    *
    * Может полностью поменять его структуру, что не указываем - не меняет.
    * @param id
    *   блока
    * @param request
    *   запрос изменения блока (не отличается от создания)
    * @return
    *   Обновлённый блок. Подробнее см. [[NotionBlockClient]]
    */
  def update(
      id: NotionBlockId,
      request: NotionBlockRequest,
  ): OptionT[F, NotionBlockResponse]

  /** Удаляет блок.
    * @param id
    *   блока
    * @return
    *   Удалённый блок. Подробнее см. [[NotionBlockClient]]
    */
  def delete(id: NotionBlockId): OptionT[F, NotionBlockResponse]
}

object NotionBlockClient {
  def http[F[_]: Async](
      backend: SttpBackend[F, Any],
      config: NotionConfiguration,
  ): NotionBlockClient[F] =
    new NotionBlockHttpClient[F](backend, config)
}

final class NotionBlockHttpClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    implicit val config: NotionConfiguration,
) extends NotionBlockClient[F] {
  private val blocks = s"${config.url}/$v1/blocks"

  override def append(
      pageId: PageId,
      children: List[BlockRequest],
  ): OptionT[F, PaginatedList[BlockResponse]] =
    OptionT(
      basicRequestWithHeaders
        .patch(uri"$blocks/$pageId/children")
        .body(children)
        .response(unwrap[F, PaginatedList[BlockResponse]])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def get(id: BlockId): OptionT[F, BlockResponse] =
    OptionT(
      basicRequestWithHeaders
        .get(uri"$blocks/$id")
        .response(unwrap[F, BlockResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def getContent(id: PageId): OptionT[F, List[BlockResponse]] = {
    def tick(cursor: Option[Cursor]): OptionT[F, PaginatedList[BlockResponse]] =
      OptionT(
        basicRequestWithHeaders
          .get(uri"$blocks/$id/children?${cursor.map(_.toQuery)}")
          .response(unwrap[F, PaginatedList[BlockResponse]])
          .readTimeout(config.timeout)
          .send(sttpBackend)
          .flatMap(optionIfSuccess(_)),
      )

    concatPaginatedLists(tick)
  }

  override def update(id: BlockId, request: BlockRequest): OptionT[F, BlockResponse] =
    OptionT(
      basicRequestWithHeaders
        .patch(uri"$blocks/$id")
        .response(unwrap[F, BlockResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def delete(id: BlockId): OptionT[F, BlockResponse] =
    OptionT(
      basicRequestWithHeaders
        .delete(uri"$blocks/$id")
        .response(unwrap[F, BlockResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )
}
