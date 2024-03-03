package org.kote.domain.comment

import org.kote.common.tethys.TethysInstances
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.content.file.File.FileId
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import java.time.Instant
import scala.annotation.nowarn

final case class CommentResponse(
    id: CommentId,
    author: UserId,
    text: String,
    files: List[FileId],
    createdAt: Instant,
)

object CommentResponse extends TethysInstances {
  @nowarn
  implicit val commentResponseReader: JsonReader[CommentResponse] = jsonReader

  @nowarn
  implicit val commentResponseWriter: JsonWriter[CommentResponse] = jsonWriter

  implicit val commentResponseSchema: Schema[CommentResponse] =
    Schema.derived.description("Комментарий")
}
