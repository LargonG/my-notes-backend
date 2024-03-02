package org.kote.service

import cats.FlatMap
import cats.data.OptionT
import cats.effect.kernel.Clock
import cats.effect.std.UUIDGen
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.comment.{Comment, CommentResponse, CreateComment}
import org.kote.domain.task.Task.TaskId
import org.kote.repository.{CommentRepository, TaskRepository}
import cats.syntax.flatMap._
import cats.syntax.functor._

trait CommentService[F[_]] {
  def create(createComment: CreateComment): F[CommentResponse]

  def list(id: TaskId): OptionT[F, List[CommentId]]

  def get(id: CommentId): OptionT[F, CommentResponse]

  def delete(id: CommentId): OptionT[F, CommentResponse]
}

final case class RepositoryCommentService[F[_]: UUIDGen: FlatMap: Clock](
    taskRepository: TaskRepository[F],
    commentRepository: CommentRepository[F],
) extends CommentService[F] {
  override def create(createComment: CreateComment): F[CommentResponse] =
    for {
      uuid <- UUIDGen[F].randomUUID
      date <- Clock[F].realTimeInstant
      comment = Comment.fromCreateComment(uuid, date, createComment)
      _ <- commentRepository.create(comment)
    } yield comment.toResponse

  override def list(id: TaskId): OptionT[F, List[CommentId]] = {
    for {
      task <- taskRepository.get(id)
    } yield task.comments
  }

  override def get(id: CommentId): OptionT[F, CommentResponse] =
    commentRepository.get(id).map(_.toResponse)

  override def delete(id: CommentId): OptionT[F, CommentResponse] =
    commentRepository.delete(id).map(_.toResponse)
}
