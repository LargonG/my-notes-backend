package org.kote.client.notion

import cats.effect.kernel.Async
import cats.implicits.{catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import org.kote.client.notion
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.user.{UserId, UserResponse}
import sttp.client3.{Empty, RequestT, SttpBackend, UriContext, basicRequest}

trait NotionUserClient[F[_]] {
  def list: F[List[UserResponse]]

  def get(id: UserId): F[Option[UserResponse]]

  def me: F[Option[UserResponse]]
}

final class NotionUserHttpClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    implicit val config: NotionConfiguration,
) extends NotionUserClient[F] {
  private val users = s"${config.url}/${notion.v1}/users"

  override def list: F[List[UserResponse]] = {
    def loop(acc: List[List[UserResponse]], cursor: Option[String]): F[List[List[UserResponse]]] =
      paginatedList(cursor).flatMap { value =>
        if (value.hasMore) loop(value.results :: acc, value.nextCursor)
        else (value.results :: acc).pure
      }

    loop(List(), None).map(_.flatten)
  }

  override def get(id: UserId): F[Option[UserResponse]] =
    basicRequestWithHeaders
      .get(uri"$users/$id")
      .response(notion.unwrap[F, Option[UserResponse]])
      .readTimeout(config.timeout)
      .send(sttpBackend)
      .flatMap(_.body)

  override def me: F[Option[UserResponse]] =
    basicRequestWithHeaders
      .get(uri"$users/me")
      .response(notion.unwrap[F, Option[UserResponse]])
      .readTimeout(config.timeout)
      .send(sttpBackend)
      .flatMap(_.body)

  private def paginatedList(cursor: Option[String]): F[PaginatedList[UserResponse]] =
    basicRequestWithHeaders
      .get(if (cursor.isEmpty) uri"$users" else uri"$users?start_cursor=$cursor")
      .response(notion.unwrap[F, PaginatedList[UserResponse]])
      .readTimeout(config.timeout)
      .send(sttpBackend)
      .flatMap(_.body)

}
