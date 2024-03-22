package org.kote.client.notion.model.property

import cats.implicits.toFunctorOps
import io.circe.Decoder
import org.kote.client.notion.model.file.File
import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.page.response.PageSelectPropertyResponse
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserResponse

/** У Notion есть привычка для каждого нового endpoint объявлять всё новые и новые типы. Этот тип
  * используется в endpoint "Retrieve a page property item", может быть как списком, там и объектом
  */
sealed trait PropertyItem

object PropertyItem {
  implicit def decoder[T]: Decoder[PropertyItem] =
    List[Decoder[PropertyItem]](
      Decoder[SingleItem].widen,
      Decoder[PaginatedItem].widen,
    ).reduceLeft(_ or _)
}

case class PaginatedItem(
    inner: PaginatedList[SingleItem],
) extends PropertyItem

object PaginatedItem {
  implicit val decoder: Decoder[PaginatedItem] =
    Decoder[PaginatedList[SingleItem]].map(PaginatedItem(_))
}

case class SingleItem(id: String, value: PropertyValue) extends PropertyItem

object SingleItem {
  implicit def decoder: Decoder[SingleItem] = Decoder.instance { cur =>
    for {
      id <- cur.get[String]("id")
      valueType <- cur.get[PropertyType]("type")
      value <- cur.get[PropertyValue](valueType)
    } yield SingleItem(id, value)
  }
}

sealed trait PropertyValue

object PropertyValue {
  implicit val decoder: Decoder[PropertyValue] =
    List[Decoder[PropertyValue]](
      Decoder[RichTextPropertyValue].widen,
      Decoder[TitlePropertyValue].widen,
      Decoder[SelectPropertyValue].widen,
      Decoder[PeoplePropertyValue].widen,
      Decoder[FilesPropertyValue].widen,
    ).reduceLeft(_ or _)
}

case class RichTextPropertyValue(inner: RichText) extends PropertyValue

object RichTextPropertyValue {
  implicit val decoder: Decoder[RichTextPropertyValue] =
    Decoder[RichText].map(RichTextPropertyValue(_))
}

case class TitlePropertyValue(inner: RichText) extends PropertyValue

object TitlePropertyValue {
  implicit val decoder: Decoder[TitlePropertyValue] =
    Decoder[RichText].map(TitlePropertyValue(_))
}

case class SelectPropertyValue(inner: PageSelectPropertyResponse) extends PropertyValue

object SelectPropertyValue {
  implicit val decoder: Decoder[SelectPropertyValue] =
    Decoder[PageSelectPropertyResponse].map(SelectPropertyValue(_))
}

case class PeoplePropertyValue(inner: UserResponse) extends PropertyValue

object PeoplePropertyValue {
  implicit val decoder: Decoder[PeoplePropertyValue] =
    Decoder[UserResponse].map(PeoplePropertyValue(_))
}

// todo: в доке написана какая-то мутотень, если будет выдавать ошибку, то поправить
case class FilesPropertyValue(inner: File) extends PropertyValue

object FilesPropertyValue {
  implicit val decoder: Decoder[FilesPropertyValue] =
    Decoder[File].map(FilesPropertyValue(_))
}
