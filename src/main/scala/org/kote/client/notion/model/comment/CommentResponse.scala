package org.kote.client.notion.model.comment

import io.circe.Decoder
import org.kote.client.notion.model.parent.PageParent
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserResponse

/** Уменьшенная версия ответа notion на объект комментария
  *
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
