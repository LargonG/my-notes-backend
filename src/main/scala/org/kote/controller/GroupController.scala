package org.kote.controller

import org.kote.common.controller.Controller
import org.kote.domain.group.Group.GroupId
import org.kote.domain.group.{CreateGroup, GroupResponse}
import org.kote.domain.task.Task.TaskId
import org.kote.domain.task.TaskResponse
import org.kote.service.GroupService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint

class GroupController[F[_]](groupService: GroupService[F]) extends Controller[F] {
  private val standardPath: EndpointInput[Unit] = "api" / "v1" / "group"
  private val pathWithGroupId: EndpointInput[GroupId] = standardPath / path[GroupId]("groupId")

  private val createGroup: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать колонку")
      .in(standardPath)
      .in(jsonBody[CreateGroup])
      .out(jsonBody[Option[GroupResponse]])
      .serverLogicSuccess(groupService.create(_).value)

  private val listGroup: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить колонку")
      .in(pathWithGroupId)
      .out(jsonBody[Option[GroupResponse]])
      .serverLogicSuccess(groupService.get(_).value)

  private val moveTask: ServerEndpoint[Any, F] =
    endpoint.patch
      .summary("Переместить задачу в другую колонку")
      .in(
        standardPath / "move"
          / query[GroupId]("to")
          / query[TaskId]("what"),
      )
      .out(jsonBody[Option[TaskResponse]])
      .serverLogicSuccess { case (to, what) =>
        groupService.moveTask(to, what).value
      }

  private val deleteGroup: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить колонку")
      .in(pathWithGroupId)
      .out(jsonBody[Option[GroupResponse]])
      .serverLogicSuccess(groupService.delete(_).value)

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(createGroup, listGroup, moveTask, deleteGroup).map(_.withTag("Group"))
}

object GroupController {
  def make[F[_]](groupService: GroupService[F]): GroupController[F] =
    new GroupController[F](groupService)
}
