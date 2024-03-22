package org.kote.client.notion.model.comment

import io.circe.Encoder
import org.kote.client.notion.model.parent.PageParent
import org.kote.client.notion.model.text.RichText

/** Запрос создания нового комментария. В публичном Notion API разрешено создавать комментарии
  * только к страницам, или к существующим дискуссиям. Нам интересно только первое.
  *
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
