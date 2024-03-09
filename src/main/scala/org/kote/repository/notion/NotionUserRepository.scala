package org.kote.repository.notion

import cats.Functor
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapter, FromAdapterF, ToAdapter}
import org.kote.client.notion.{
  NotionUserClient,
  NotionUserId,
  NotionUserRequest,
  NotionUserResponse,
}
import org.kote.domain.user.User
import org.kote.domain.user.User.UserId
import org.kote.repository.UserRepository

class NotionUserRepository[F[_]: Functor](client: NotionUserClient[F])(implicit
    val userAdapter: Adapter[User, NotionUserRequest, NotionUserResponse],
    val userIdAdapter: Adapter[UserId, NotionUserId, NotionUserId],
) extends UserRepository[F] {

  /** Связывается с notion по связке user id -> notion user id
    * @param obj
    * @return
    */
  // todo: auth, можно, если public api
  override def create(obj: User): F[Long] = get(obj.id).as(1L).getOrElse(0L)

  override def all: F[List[User]] = (for {
    response <- client.list
  } yield response.fromResponse).getOrElse(List())

  override def get(id: UserId): OptionT[F, User] =
    for {
      response <- client.get(id.toRequest)
    } yield response.fromResponse

  /** В notion нельзя удалять пользователей, поэтому просто получаем пользователя.
    * @param id
    *   пользователя (внутреннее)
    * @return
    *   информация о notion аккаунте удалённого пользователя
    */
  // todo: auth, наверное
  override def delete(id: UserId): OptionT[F, User] = get(id)

  /** Notion не позволяет ничего обновлять у пользователя, так что получаем пользователя и
    * возвращаем его.
    * @param id
    *   объекта
    * @param cmds
    *   команды установки новых значений
    * @return
    *   объект после изменений, если он существовал
    */
  override def update(
      id: User.UserId,
      cmds: List[UserRepository.UserUpdateCommand],
  ): OptionT[F, User] = get(id)
}
