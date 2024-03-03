package org.kote.controller

import org.kote.common.controller.Controller
import org.kote.domain.comment.Comment.CommentId
import org.kote.domain.comment.{CommentResponse, CreateComment}
import org.kote.domain.task.Task.TaskId
import org.kote.service.CommentService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint

import java.util.UUID

class CommentController[F[_]](commentService: CommentService[F]) extends Controller[F] {
  private val standardPath: EndpointInput[Unit] = "api" / "v1" / "comment"
  private val pathWithCommentId: EndpointInput[CommentId] =
    standardPath / path[CommentId]("commentId")

  private val createComment: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать комментарий")
      .in(standardPath)
      .in(jsonBody[CreateComment])
      .out(jsonBody[CommentResponse])
      .serverLogicSuccess(commentService.create)

  private val listComments: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Посмотреть все комментарии к задаче")
      .in(standardPath / path[UUID]("taskId"))
      .out(jsonBody[Option[List[CommentResponse]]])
      .serverLogicSuccess(taskId => commentService.list(TaskId(taskId)).value)

  private val getComment: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Посмотреть комментарий")
      .in(pathWithCommentId)
      .out(jsonBody[Option[CommentResponse]])
      .serverLogicSuccess(commentService.get(_).value)

  private val deleteComment: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить комментарий")
      .in(pathWithCommentId)
      .out(jsonBody[Option[CommentResponse]])
      .serverLogicSuccess(commentService.delete(_).value)

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(createComment, getComment, listComments, deleteComment).map(_.withTag("Comment"))
}

object CommentController {
  def make[F[_]](commentService: CommentService[F]): CommentController[F] =
    new CommentController[F](commentService)
}
