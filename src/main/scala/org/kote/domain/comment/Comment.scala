package org.kote.domain.comment

import org.kote.common.tethys.TethysInstances
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.content.Content
import org.kote.domain.task.Task.TaskId
import org.kote.domain.user.User.UserId
import sttp.tapir.Schema
import tethys.{JsonReader, JsonWriter}

import java.time.Instant
import java.util.UUID

final case class Comment(
    id: CommentId,
    taskParent: TaskId,
    author: UserId,
    content: Content,
    createdAt: Instant,
) {
  def toResponse: CommentResponse =
    CommentResponse(id, taskParent, author, content, createdAt)
}

object Comment {
  def fromCreateComment(uuid: UUID, date: Instant, createComment: CreateComment): Comment =
    Comment(
      CommentId(uuid),
      createComment.parent,
      createComment.author,
      createComment.content,
      date,
    )

  final case class CommentId(inner: UUID) extends AnyVal

  object CommentId extends TethysInstances {
    implicit val commentIdReader: JsonReader[CommentId] = JsonReader[UUID].map(CommentId.apply)
    implicit val commentIdWriter: JsonWriter[CommentId] = JsonWriter[UUID].contramap(_.inner)
    implicit val commentIdSchema: Schema[CommentId] = Schema.derived.description("ID комментария")
  }
}
