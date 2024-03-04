package org.kote.client.notion.model.comment

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

final case class CommentId(inner: UUID) extends AnyVal
