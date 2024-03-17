package org.kote.client.notion.model.page.response

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.kote.client.notion.model.file.FileHeader
import org.kote.client.notion.model.property._
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserResponse

/** Общие поля для всех типов свойств страницы в базе данных
  * @param id
  *   string - случайная строка
  * @param value
  *   значение определённого типа, указанного в поле "type" в json
  */
final case class PagePropertyResponse(
    id: String,
    value: Option[PagePropertyResponseValue],
)

object PagePropertyResponse {
  implicit val pagePropertyResponseDecoder: Decoder[PagePropertyResponse] = Decoder.instance {
    cursor =>
      for {
        id <- cursor.get[String]("id")
        propertyType <- cursor.get[PropertyType]("type")
        value <- propertyType match {
          case FilesType    => cursor.get[Option[PageFilesPropertyResponse]](propertyType)
          case PeopleType   => cursor.get[Option[PagePeoplePropertyResponse]](propertyType)
          case RichTextType => cursor.get[Option[PageRichTextPropertyResponse]](propertyType)
          case StatusType   => cursor.get[Option[PageStatusPropertyResponse]](propertyType)
          case TitleType    => cursor.get[Option[PageTitlePropertyResponse]](propertyType)
          case SelectType   => cursor.get[Option[PageSelectPropertyResponse]](propertyType)
          case OtherType(_) => cursor.get[Option[PageOtherPropertyResponseValue.type]](propertyType)
        }
      } yield PagePropertyResponse(id, value)
  }
}

// Можно было, кстати, сделать enum, ну да ладно

sealed trait PagePropertyResponseValue

case object PageOtherPropertyResponseValue extends PagePropertyResponseValue {
  implicit val decoder: Decoder[PageOtherPropertyResponseValue.type] =
    Decoder.decodeJson.map(_ => PageOtherPropertyResponseValue)
}

final case class PageFilesPropertyResponse(files: List[FileHeader])
    extends PagePropertyResponseValue

object PageFilesPropertyResponse {
  implicit val pageFilesPropertyResponseDecoder: Decoder[PageFilesPropertyResponse] =
    deriveDecoder
}

final case class PagePeoplePropertyResponse(people: List[UserResponse])
    extends PagePropertyResponseValue

object PagePeoplePropertyResponse {
  implicit val pagePeoplePropertyResponseDecoder: Decoder[PagePeoplePropertyResponse] =
    Decoder[List[UserResponse]].map(PagePeoplePropertyResponse.apply)
}

final case class PageRichTextPropertyResponse(text: List[RichText])
    extends PagePropertyResponseValue

object PageRichTextPropertyResponse {
  implicit val pageRichTextPropertyResponseDecoder: Decoder[PageRichTextPropertyResponse] =
    Decoder.decodeList[RichText].map(PageRichTextPropertyResponse(_))
}

final case class PageStatusPropertyResponse(id: String, name: String)
    extends PagePropertyResponseValue

object PageStatusPropertyResponse {
  implicit val pageStatusPropertyResponse: Decoder[PageStatusPropertyResponse] =
    Decoder.instance { source =>
      for {
        id <- source.get[String]("id")
        name <- source.get[String]("name")
      } yield PageStatusPropertyResponse(id, name)
    }
}

final case class PageTitlePropertyResponse(text: List[RichText]) extends PagePropertyResponseValue

object PageTitlePropertyResponse {
  implicit val pageTitlePropertyResponse: Decoder[PageTitlePropertyResponse] =
    Decoder.decodeList[RichText].map(PageTitlePropertyResponse(_))
}

final case class PageSelectPropertyResponse(id: String, name: String)
    extends PagePropertyResponseValue

object PageSelectPropertyResponse {
  implicit val pageSelectPropertyResponse: Decoder[PageSelectPropertyResponse] = deriveDecoder
}
