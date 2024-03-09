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
  def list: OptionT[F, List[NotionUserResponse]]

  def get(id: NotionUserId): OptionT[F, NotionUserResponse]

  def me: OptionT[F, NotionUserResponse]
}

final class NotionUserHttpClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    implicit val config: NotionConfiguration,
) extends NotionUserClient[F] {
  private val users = s"${config.url}/${notion.v1}/users"

  override def list: OptionT[F, List[UserResponse]] = {
    def tick(cursor: Option[Cursor]): OptionT[F, PaginatedList[UserResponse]] =
      OptionT(
        basicRequestWithHeaders
          .get(uri"$users?$cursor")
          .response(notion.unwrap[F, PaginatedList[UserResponse]])
          .readTimeout(config.timeout)
          .send(sttpBackend)
          .flatMap(optionIfSuccess(_)),
      )

    concatPaginatedLists(tick)
  }

  override def get(id: UserId): OptionT[F, UserResponse] =
    OptionT(
      basicRequestWithHeaders
        .get(uri"$users/$id")
        .response(notion.unwrap[F, UserResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )

  override def me: OptionT[F, UserResponse] =
    OptionT(
      basicRequestWithHeaders
        .get(uri"$users/me")
        .response(notion.unwrap[F, UserResponse])
        .readTimeout(config.timeout)
        .send(sttpBackend)
        .flatMap(optionIfSuccess(_)),
    )
}
