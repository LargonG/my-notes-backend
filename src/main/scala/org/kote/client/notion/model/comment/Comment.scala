package org.kote.client.notion.model.comment

import io.circe.{Decoder, Encoder}
import org.kote.client.notion.model.parent.PageParent
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserResponse

import java.util.UUID

/** Уменьшенная версия ответа notion на объект комментария
  * @param id
  *   UUID
  * @param parent
  *   PageParent - в расширенной версии может быть как PageParent, так и BlockParent
  * @param createdBy
  *   PartialUser - краткие данные о пользователе
  * @param richText
  *   Rich text object
  */
final case class CommentResponse(
    id: CommentId,
    parent: PageParent,
    createdBy: UserResponse,
    richText: RichText,
)

object CommentResponse {
  implicit val commentResponseDecoder: Decoder[CommentResponse] =
    Decoder.forProduct4("id", "parent", "created_by", "rich_text")(CommentResponse.apply)
}

/** Запрос создания нового комментария. В публичном Notion API разрешено создавать комментарии
  * только к страницам, или к существующим дискуссиям. Нам интересно только первое.
  * @param parent
  *   Page parent - UUID of Page
  * @param richText
  *   Rich text object
  */
final case class CommentRequest(
    parent: PageParent,
    richText: RichText,
)

object CommentRequest {
  implicit val commentRequestEncoder: Encoder[CommentRequest] =
    Encoder.forProduct2("parent", "rich_text") { source =>
      (source.parent, source.richText)
    }
}

final case class CommentId(inner: UUID) extends AnyVal

object CommentId {
  implicit val commentIdEncoder: Encoder[CommentId] =
    Encoder.encodeUUID.contramap(_.inner)

  implicit val commentIdDecoder: Decoder[CommentId] =
    Decoder.decodeUUID.map(CommentId(_))
}
