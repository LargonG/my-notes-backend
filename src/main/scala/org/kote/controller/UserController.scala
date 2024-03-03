package org.kote.controller

import org.kote.common.controller.Controller
import org.kote.domain.user.User.UserId
import org.kote.domain.user.{CreateUser, UnsafeUserResponse, UserResponse}
import org.kote.service.UserService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint

class UserController[F[_]](userService: UserService[F]) extends Controller[F] {
  private val standardPath: EndpointInput[Unit] = "api" / "v1" / "user"
  private val pathWithUserId: EndpointInput[UserId] = standardPath / path[UserId]("userId")

  private val createUser: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать пользователя")
      .in(standardPath)
      .in(jsonBody[CreateUser])
      .out(jsonBody[UnsafeUserResponse])
      .serverLogicSuccess(userService.create)

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

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(createUser, listUsers, getUser, deleteUser).map(_.withTag("User"))
}

object UserController {
  def make[F[_]](userService: UserService[F]): UserController[F] =
    new UserController[F](userService)
}
