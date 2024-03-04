package org.kote.client.notion.model.page

import org.kote.client.notion.model.file.File
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.model.user.UserRequest

sealed trait PagePropertyRequest

final case class PageFilesPropertyRequest(files: List[File]) extends PagePropertyRequest

final case class PagePeoplePropertyRequest(people: List[UserRequest]) extends PagePropertyRequest

final case class PageRichTextPropertyRequest(richText: List[RichText]) extends PagePropertyRequest

final case class PageStatusPropertyRequest(name: String) extends PagePropertyRequest

final case class PageSelectPropertyRequest(name: String) extends PagePropertyRequest

final case class PageTitlePropertyRequest(title: List[RichText]) extends PagePropertyRequest
