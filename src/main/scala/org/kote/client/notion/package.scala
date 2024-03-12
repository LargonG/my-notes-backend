package org.kote.client

import cats.data.OptionT
import cats.implicits.toFunctorOps
import cats.{Applicative, ApplicativeThrow, Monad}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}
import org.kote.client.notion.configuration.NotionConfiguration
import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.list.PaginatedList.Cursor
import sttp.client3.circe.asJsonAlways
import sttp.client3.{Empty, RequestT, Response, ResponseAs, basicRequest}

/** Пакет предоставляющий клиентов для работы с notion api.
  *
  * Реализованы только те части, которые используются основным приложением
  *
  * ==Соглашение о возвращаемых значениях==
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
package object notion {
  // Database //
  type NotionDatabaseId = model.database.DbId

  type NotionDatabaseCreateRequest = model.database.DbRequest
  type NotionDatabasePropertiesUpdateRequest = model.database.DbUpdateRequest
  type NotionDatabaseSearchRequest = model.database.DbSearchRequest

  type NotionDatabasePropertiesUpdateResponse = model.database.DbResponse
  type NotionDatabaseResponse = model.database.DbResponse

  // Page //
  type NotionPageId = model.page.PageId

  type NotionPageCreateRequest = model.page.PageRequest
  type NotionPagePropertiesUpdateRequest = model.page.PageUpdateRequest
  type NotionPageSearchRequest = model.page.PageSearchRequest

  type NotionPagePropertyItemResponse = model.property.PropertyItem
  type NotionPageResponse = model.page.PageResponse

  // Block //
  type NotionBlockId = model.block.BlockId
  type NotionBlockRequest = model.block.BlockRequest
  type NotionBlockResponse = model.block.BlockResponse

  // Comment //
  type NotionCommentId = model.comment.CommentId
  type NotionCommentCreateRequest = model.comment.CommentRequest

  // File //
  type NotionFileHeader = model.file.FileHeader
  type NotionCommentResponse = model.comment.CommentResponse
  type NotionInternalFile = model.file.NotionFile
  type NotionExternalFile = model.file.ExternalFile

  // Text //
  type NotionRichText = model.text.RichText
  type NotionText = model.text.Text

  // User //
  type NotionUserId = model.user.UserId

  type NotionUserRequest = model.user.UserRequest
  type NotionUserResponse = model.user.UserResponse

  ////////////////
  // Версии api //
  ////////////////

  private[notion] val v1 = "v1"

  private[notion] def optionToString[T](opt: Option[T]): String = opt match {
    case Some(value) => value.toString
    case None        => ""
  }

  private[notion] def optionEncode[T: Encoder](
      key: String,
      opt: Option[T],
  ): Option[(String, Json)] =
    opt.map(value => key -> value.asJson)

  private[notion] def unwrap[F[_]: ApplicativeThrow, T: Decoder]: ResponseAs[F[T], Any] =
    asJsonAlways[T].map(ApplicativeThrow[F].fromEither(_))

  private[notion] def basicRequestWithHeaders(implicit
      config: NotionConfiguration,
  ): RequestT[Empty, Either[String, String], Any] =
    basicRequest
      .header("Authorization", s"Bearer ${config.apiKey}")
      .header("Notion-Version", config.notionVersion)
      .contentType("application/json")

  private[notion] def optionIfSuccess[F[_]: ApplicativeThrow, T](
      response: Response[F[T]],
  ): F[Option[T]] =
    if (response.isSuccess) {
      response.body.map(Option(_))
    } else if (response.isServerError || response.code.code == 429) {
      Applicative[F].pure(Option.empty[T])
    } else if (response.isClientError) {
      ApplicativeThrow[F].raiseError(
        new IllegalArgumentException(
          s"${response.code} ${response.statusText} ${response.body}",
        ),
      )
    } else {
      ApplicativeThrow[F].raiseError(new IllegalStateException())
    }

  private[notion] def encodeType[T](value: String): Encoder[T] =
    Encoder.encodeString.contramap(_ => value)

  private[notion] def decodeType[T](expected: String, constructor: String => T): Decoder[T] =
    Decoder.decodeString.emap(actual =>
      if (actual == expected) Right(constructor(actual))
      else Left("Unsupported type"),
    )

  private[notion] def concatPaginatedLists[F[_]: Monad, T](
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
            OptionT.pure[F]((response.results :: acc).reverse),
        )
        .orElse(
          if (acc.nonEmpty) OptionT.pure[F](acc.reverse)
          else OptionT.none[F, List[List[T]]],
        )

    loop(List(), None).map(_.flatten)
  }
}
