package org.kote.client

import cats.data.OptionT
import cats.implicits.toFunctorOps
import cats.{Applicative, ApplicativeThrow, Monad}
import io.circe.{Decoder, Encoder}
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.list.PaginatedList.Cursor
import sttp.client3.circe.asJsonAlways
import sttp.client3.{Empty, RequestT, Response, ResponseAs, basicRequest}

package object notion {
  type NotionPageId = model.page.PageId
  type NotionCommentId = model.comment.CommentId
  type NotionCommentRequest = model.comment.CommentRequest
  type NotionCommentResponse = model.comment.CommentResponse
  type NotionUserRequest = model.user.UserRequest
  type NotionUserResponse = model.user.UserResponse
  type NotionUserId = model.user.UserId

  val v1 = "api/v1"

  def unwrap[F[_]: ApplicativeThrow, T: Decoder]: ResponseAs[F[T], Any] =
    asJsonAlways[T].map(ApplicativeThrow[F].fromEither(_))

  def basicRequestWithHeaders(implicit
      config: NotionConfiguration,
  ): RequestT[Empty, Either[String, String], Any] =
    basicRequest
      .header("Authorization", s"Bearer ${config.apiKey}")
      .header("Notion-Version", config.notionVersion)
      .contentType("application/json")

  def optionIfSuccess[F[_]: Applicative, T](response: Response[F[T]]): F[Option[T]] =
    if (response.isSuccess) {
      response.body.map(Option(_))
    } else {
      Applicative[F].pure(Option.empty[T])
    }

  def encodeType[T](value: String): Encoder[T] =
    Encoder.encodeString.contramap(_ => value)

  def decodeType[T](expected: String, constructor: String => T): Decoder[T] =
    Decoder.decodeString.emap(actual =>
      if (actual == expected) Right(constructor(actual))
      else Left("Unsupported type"),
    )

  def concatPaginatedLists[F[_]: Monad, T](
      tick: Option[Cursor] => OptionT[F, PaginatedList[T]],
  ): OptionT[F, List[T]] = {
    def loop(
        acc: List[List[T]],
        cursor: Option[Cursor],
    ): OptionT[F, List[List[T]]] =
      tick(cursor)
        .flatMap(response =>
          if (response.hasMore)
            loop(
              response.results :: acc,
              for {
                next <- response.nextCursor
                prev <- cursor.orElse(Some(Cursor(next)))
              } yield Cursor(next, prev.pageSize),
            )
          else
            OptionT.pure[F](acc.reverse),
        )
        .orElse(
          if (acc.nonEmpty) OptionT.pure[F](acc.reverse)
          else OptionT.none[F, List[List[T]]],
        )

    loop(List(), None).map(_.flatten)
  }
}
