package org.kote.service

import cats.{Monad, MonadThrow}
import cats.data.OptionT
import cats.effect.kernel.Clock
import cats.effect.std.UUIDGen
import cats.implicits.toTraverseOps
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.client.notion.{NotionPageClient, NotionPageId, NotionUserClient, NotionUserId}
import org.kote.domain.user.User.UserId
import org.kote.domain.user.{CreateUser, UnsafeUserResponse, User, UserResponse}
import org.kote.repository.{
  BoardRepository,
  GroupRepository,
  IntegrationRepository,
  TaskRepository,
  UserRepository,
}
import org.kote.repository.UserRepository.UserUpdateCommand
import org.kote.service.notion.v1.NotionUserService

trait UserService[F[_]] {

  /** Создаёт нового пользователя
    *
    * @param createUser
    *   объект создания
    * @return
    *   нового пользователя
    */
  def create(createUser: CreateUser): F[UnsafeUserResponse]

  // todo: вообще, вызов этого метода не безопасен, так как в случае
  //  обновления trello или notion должна происходить умная логика импорта зависимостей, а её будет
  //  криво реализовывать в этом методе, поэтому его нужно убрать

  /** Обновляет поля у пользователя
    *
    * @param id
    *   пользователя
    * @param cmds
    *   команда обновления
    * @return
    *   пользователя с новыми данными
    */
  def update(id: UserId, cmds: List[UserUpdateCommand]): OptionT[F, UnsafeUserResponse]

  /** Список всех пользователей, что есть на сайте
    *
    * @return
    */
  def list: F[List[UserResponse]]

  /** Информация о пользователе для стороннего наблюдателя
    *
    * @param id
    *   пользователя
    * @return
    *   общая информация, без личный данных
    */
  def get(id: UserId): OptionT[F, UserResponse]

  /** Полная информация о пользователе, с его паролем и личными данными Следует использовать только
    * когда эту информацию хочет получить сам пользователь
    *
    * @param id
    *   пользователя
    * @return
    *   полная информация о пользователе
    */
  def unsafeGet(id: UserId): OptionT[F, UnsafeUserResponse]

  /** Удаляет пользователя вместе с его досками, если они были связаны с notion и trello, то там они
    * остаются. Доступ к этой функции имеет только админ и сам пользователь
    *
    * @param id
    *   пользователя
    * @return
    *   полную информацию удалённого пользователя
    */
  def delete(id: UserId): OptionT[F, UnsafeUserResponse]
}

object UserService {
  def fromRepository[F[_]: UUIDGen: Monad: Clock](
      userRepository: UserRepository[F],
      boardRepository: BoardRepository[F],
      groupRepository: GroupRepository[F],
      taskRepository: TaskRepository[F],
  ): UserService[F] =
    new RepositoryUserService[F](userRepository, boardRepository, groupRepository, taskRepository)

  def syncNotion[F[_]: UUIDGen: MonadThrow: Clock](
      userRepository: UserRepository[F],
      notionUserClient: NotionUserClient[F],
      notionPageClient: NotionPageClient[F],
      userToNotionUserIntegration: IntegrationRepository[F, UserId, NotionUserId],
      userMainPageIntegration: IntegrationRepository[F, UserId, NotionPageId],
  ): UserService[F] =
    new NotionUserService[F](
      userRepository,
      notionUserClient,
      notionPageClient,
      userToNotionUserIntegration,
      userMainPageIntegration,
    )
}

class RepositoryUserService[F[_]: UUIDGen: Monad: Clock](
    userRepository: UserRepository[F],
    boardRepository: BoardRepository[F],
    groupRepository: GroupRepository[F],
    taskRepository: TaskRepository[F],
) extends UserService[F] {
  override def create(createUser: CreateUser): F[UnsafeUserResponse] =
    for {
      uuid <- UUIDGen[F].randomUUID
      date <- Clock[F].realTimeInstant
      user = User.fromCreateUser(uuid, date, createUser)
      _ <- userRepository.create(user)
    } yield user.toUnsafeResponse()

  override def update(id: UserId, cmds: List[UserUpdateCommand]): OptionT[F, UnsafeUserResponse] =
    userRepository.update(id, cmds).map(_.toUnsafeResponse())

  override def list: F[List[UserResponse]] =
    userRepository.all.map(_.map(_.toResponse))

  override def get(id: UserId): OptionT[F, UserResponse] =
    userRepository.get(id).map(_.toResponse)

  override def unsafeGet(id: UserId): OptionT[F, UnsafeUserResponse] =
    userRepository.get(id).map(_.toUnsafeResponse())

  override def delete(id: UserId): OptionT[F, UnsafeUserResponse] =
    for {
      deleted <- userRepository.delete(id)
      boards <- boardRepository.list(deleted.id)
      _ <- boards.traverse(board => boardRepository.delete(board.id))
      groupIds = boards.flatMap(_.groups)
      groups <- groupIds.traverse(groupRepository.delete)
      taskIds = groups.flatMap(_.tasks)
      _ <- taskIds.traverse(taskRepository.delete)
    } yield deleted.toUnsafeResponse()
}
