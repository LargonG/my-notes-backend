package org.kote.client.notion.model.page

import io.circe.{Encoder, Json}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import org.kote.client.notion.model.file.FileHeader
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserRequest

sealed trait PagePropertyRequest

object PagePropertyRequest {
  implicit val pagePropertyRequestEncoder: Encoder[PagePropertyRequest] =
    Encoder.instance {
      case files @ PageFilesPropertyRequest(_)       => Json.obj("files" -> files.asJson)
      case people @ PagePeoplePropertyRequest(_)     => Json.obj("people" -> people.asJson)
      case richText @ PageRichTextPropertyRequest(_) => Json.obj("rich_text" -> richText.asJson)
      case status @ PageStatusPropertyRequest(_)     => Json.obj("status" -> status.asJson)
      case select @ PageSelectPropertyRequest(_)     => Json.obj("select" -> select.asJson)
      case title @ PageTitlePropertyRequest(_)       => Json.obj("title" -> title.asJson)
    }
}

final case class PageFilesPropertyRequest(files: List[FileHeader]) extends PagePropertyRequest

object PageFilesPropertyRequest {
  implicit val pageFilesPropertyRequestEncoder: Encoder[PageFilesPropertyRequest] =
    Encoder.encodeList[FileHeader].contramap(_.files)
}

final case class PagePeoplePropertyRequest(people: List[UserRequest]) extends PagePropertyRequest

object PagePeoplePropertyRequest {
  implicit val pagePeoplePropertyRequestEncoder: Encoder[PagePeoplePropertyRequest] =
    deriveEncoder
}

final case class PageRichTextPropertyRequest(richText: List[RichText]) extends PagePropertyRequest

object PageRichTextPropertyRequest {
  implicit val pageRichTextPropertyRequestEncoder: Encoder[PageRichTextPropertyRequest] =
    Encoder.instance(source => Json.obj("rich_text" -> source.richText.asJson))
}

final case class PageStatusPropertyRequest(name: String) extends PagePropertyRequest

object PageStatusPropertyRequest {
  implicit val pageStatusPropertyRequestEncoder: Encoder[PageStatusPropertyRequest] =
    deriveEncoder
}

final case class PageSelectPropertyRequest(name: String) extends PagePropertyRequest

object PageSelectPropertyRequest {
  implicit val pageSelectPropertyRequest: Encoder[PageSelectPropertyRequest] =
    deriveEncoder
}

final case class PageTitlePropertyRequest(title: List[RichText]) extends PagePropertyRequest

object PageTitlePropertyRequest {
  implicit val pageTitlePropertyRequest: Encoder[PageTitlePropertyRequest] =
    deriveEncoder
}
