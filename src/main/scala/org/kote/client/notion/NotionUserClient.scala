package org.kote.client.notion

import cats.data.OptionT
import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import org.kote.client.notion
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.list.PaginatedList.Cursor
import org.kote.client.notion.model.user.{UserId, UserResponse}
import sttp.client3.{SttpBackend, UriContext}

trait NotionUserClient[F[_]] {
  def list: F[List[NotionUserResponse]]

  def get(id: NotionUserId): F[Option[NotionUserResponse]]

  def me: F[Option[NotionUserResponse]]
}

final class NotionUserHttpClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    implicit val config: NotionConfiguration,
) extends NotionUserClient[F] {
  private val users = s"${config.url}/${notion.v1}/users"

  override def list: F[List[UserResponse]] = {
    def tick(cursor: Option[Cursor]): OptionT[F, PaginatedList[UserResponse]] =
      OptionT(
        basicRequestWithHeaders
          .get(uri"$users?$cursor")
          .response(notion.unwrap[F, PaginatedList[UserResponse]])
          .readTimeout(config.timeout)
          .send(sttpBackend)
          .flatMap(optionIfSuccess(_)),
      )

    concatPaginatedLists(tick).getOrElse(List())
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

}
