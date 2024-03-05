package org.kote.client.notion

import cats.data.OptionT
import cats.effect.kernel.Async
import cats.implicits.{toFlatMapOps, toFunctorOps}
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.database.{DbId, DbRequest, DbResponse}
import org.kote.client.notion.model.page.PageResponse
import sttp.client3.circe._
import sttp.client3.{SttpBackend, UriContext}

trait NotionDatabaseClient[F[_]] {

  /** Создаёт новую базу данных в notion
    * @param request
    *   запрос на создание базы данных
    * @return
    *   ответ сервера notion, только что созданная база данных
    */
  def create(request: DbRequest): F[DbResponse]

  /** Получает уже существующую базу данных из notion, её характеристику
    * @param id
    *   базы данных в notion
    * @return
    *   характеристику базы данных, если она существует
    */
  def get(id: DbId): OptionT[F, DbResponse]

  /** Список всех страниц, относящихся к базе данных (т.е. у которых parent выставлен на id)
    * @param id
    *   базы данных в notion
    * @return
    *   Список всех страниц, любой длины, если существует база данных с таким id и мы имеем к ней
    *   доступ
    */
  def list(id: DbId): OptionT[F, List[PageResponse]]
}

final class NotionDatabaseHttpClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    implicit val config: NotionConfiguration,
) extends NotionDatabaseClient[F] {
  private val database = s"${config.url}/$v1/databases"

  /** Создаёт новую базу данных в notion "Create" endpoint в notion api
    * @param request
    *   запрос на создание базы данных
    * @return
    *   ответ сервера notion, только что созданная база данных
    */
  override def create(request: DbRequest): F[DbResponse] =
    basicRequestWithHeaders
      .post(uri"$database")
      .body(request)
      .response(unwrap[F, DbResponse])
      .readTimeout(config.timeout)
      .send(sttpBackend)
      .flatMap(_.body)

  /** Получает уже существующую базу данных из notion, её характеристику "Retrieve" endpoint в
    * notion api
    * @param id
    *   базы данных в notion
    * @return
    *   характеристику базы данных, если она существует
    */
  override def get(id: DbId): OptionT[F, DbResponse] =
    OptionT(
      basicRequestWithHeaders
        .get(uri"$database/$id")
        .response(unwrap[F, DbResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .map(optionIfNowSuccess(_))
        .flatten,
    )

  /** Список всех страниц, относящихся к базе данных (т.е. у которых parent выставлен на id) "Query"
    * endpoint в notion api (может быть вызван несколько раз)
    * @param id
    *   базы данных в notion
    * @return
    *   Список всех страниц, любой длины, если существует база данных с таким id и мы имеем к ней
    *   доступ
    */
  override def list(id: DbId): OptionT[F, List[PageResponse]] = ???
}
