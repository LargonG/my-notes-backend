package org.kote.controller

import org.kote.common.controller.Controller
import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.TaskId
import org.kote.domain.task.{CreateTask, TaskResponse}
import org.kote.service.TaskService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint

class TaskController[F[_]](taskService: TaskService[F]) extends Controller[F] {
  private val standardPath: EndpointInput[Unit] = "api" / "v1" / "task"
  private val standardPathWithTaskId: EndpointInput[TaskId] = standardPath / path[TaskId]("taskId")

  private val createTask: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать задачу")
      .in(standardPath)
      .in(jsonBody[CreateTask])
      .out(jsonBody[Option[TaskResponse]])
      .serverLogicSuccess(taskService.create(_).value)

  private val boardListTasks: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список задач таблицы")
      .in(standardPath / query[BoardId]("board_id"))
      .out(jsonBody[Option[List[TaskResponse]]])
      .serverLogicSuccess(taskService.list(_).value)

  private val columnListTasks: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список задач колонки")
      .in(standardPath / query[GroupId]("column_id"))
      .out(jsonBody[Option[List[TaskResponse]]])
      .serverLogicSuccess(taskService.listByGroup(_).value)

  private val statusListTasks: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список задач по статусу")
      .in(standardPath / query[BoardId]("board_id") / query[String]("status"))
      .out(jsonBody[Option[List[TaskResponse]]])
      .serverLogicSuccess { case (boardId, status) =>
        taskService.listByStatus(boardId, status).value
      }
  private val getTask: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить задачу")
      .in(standardPathWithTaskId)
      .out(jsonBody[Option[TaskResponse]])
      .serverLogicSuccess(taskService.get(_).value)

  private val deleteTask: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить задачу")
      .in(standardPathWithTaskId)
      .out(jsonBody[Option[TaskResponse]])
      .serverLogicSuccess(taskService.delete(_).value)

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(createTask, boardListTasks, columnListTasks, statusListTasks, getTask, deleteTask).map(
      _.withTag("Task"),
    )
}

object TaskController {
  def make[F[_]](taskService: TaskService[F]): TaskController[F] =
    new TaskController[F](taskService)
}
