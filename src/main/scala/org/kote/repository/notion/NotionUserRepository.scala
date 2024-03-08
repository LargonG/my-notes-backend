package org.kote.repository.notion

import cats.Functor
import cats.data.OptionT
import cats.implicits.toFunctorOps
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapterF, ToAdapter}
import org.kote.client.notion
import org.kote.client.notion.NotionUserClient
import org.kote.domain.user.User
import org.kote.domain.user.User.UserId
import org.kote.repository.UserRepository
import org.kote.repository.notion.NotionUserRepository._

class NotionUserRepository[F[_]: Functor](client: NotionUserClient[F])(implicit
    val userAdapter: Adapter[User, NotionUserRequest, NotionUserResponse],
    val userIdAdapter: Adapter[UserId, NotionUserId, NotionUserId],
) extends UserRepository[F] {

  /** Связывается с notion по связке user id -> notion user id
    * @param obj
    * @return
    */
  override def create(obj: User): F[Long] = ??? // auth

  override def list: F[List[User]] = for {
    response <- client.list
  } yield response.fromResponse

  override def get(id: UserId): OptionT[F, User] =
    OptionT(for {
      response <- client.get(id.toRequest)
    } yield response.fromResponse)

  /** Удаляет связку user id -> notion user id
    * @param id
    * @return
    */
  override def delete(id: UserId): OptionT[F, User] = ??? // auth

  /** Может обновить связку user id -> notion user id
    */
  override def update(
      id: User.UserId,
      cmds: List[UserRepository.UserUpdateCommand],
  ): OptionT[F, User] = ??? // auth
}

object NotionUserRepository {
  private type NotionUserRequest = notion.model.user.UserRequest
  private type NotionUserResponse = notion.model.user.UserResponse
  private type NotionUserId = notion.model.user.UserId
}
