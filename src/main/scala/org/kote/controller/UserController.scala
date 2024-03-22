package org.kote.controller

import org.kote.common.controller.Controller
import org.kote.common.tethys.TethysInstances
import org.kote.domain.user.User.UserId
import org.kote.domain.user.{CreateUser, NotionUser, UnsafeUserResponse, UserResponse}
import org.kote.service.UserService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint

import java.util.UUID

class UserController[F[_]](userService: UserService[F]) extends Controller[F] with TethysInstances {
  private val standardPath: EndpointInput[Unit] = "api" / "v1" / "user"
  private val pathWithUserId: EndpointInput[UserId] = standardPath / path[UserId]("userId")

  private val createUser: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать пользователя")
      .in(standardPath)
      .in(jsonBody[CreateUser])
      .out(jsonBody[Option[UnsafeUserResponse]])
      .serverLogicSuccess(userService.create(_).value)

  private val listUsers: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список пользователей")
      .in(standardPath)
      .out(jsonBody[List[UserResponse]])
      .serverLogicSuccess(_ => userService.list)

  private val getUser: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить полные данные пользователя")
      .in(pathWithUserId)
      .out(jsonBody[Option[UnsafeUserResponse]])
      .serverLogicSuccess(userService.unsafeGet(_).value)

  private val deleteUser: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить пользователя")
      .in(pathWithUserId)
      .out(jsonBody[Option[UnsafeUserResponse]])
      .serverLogicSuccess(userService.delete(_).value)

  private val linkExternalUser: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Связать пользователя со сторонним клиентом")
      .in(
        pathWithUserId / query[Option[UUID]]("notion_user_id") / query[Option[String]](
          "notion_user_name",
        ),
      )
      .out(jsonBody[Option[UnsafeUserResponse]])
      .serverLogicSuccess(tuple =>
        userService
          .linkToExternalUser(
            tuple._1,
            NotionUser(tuple._2.map(org.kote.client.notion.model.user.UserId.apply), tuple._3),
          ),
      )

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(createUser, listUsers, getUser, deleteUser, linkExternalUser).map(_.withTag("User"))
}

object UserController {
  def make[F[_]](userService: UserService[F]): UserController[F] =
    new UserController[F](userService)
}
