package org.kote.service.notion.v1

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.kernel.Clock
import cats.effect.std.UUIDGen
import cats.implicits.toTraverseOps
import cats.syntax.functor._
import cats.syntax.flatMap._
import org.kote.client.notion.model.page.request.PageSearchRequest
import org.kote.client.notion.model.parent.WorkspaceParent
import org.kote.client.notion.{NotionPageClient, NotionPageId, NotionUserClient, NotionUserId}
import org.kote.domain.user.User.UserId
import org.kote.domain.user.{
  CreateUser,
  ExternalUser,
  NotionUser,
  UnsafeUserResponse,
  User,
  UserResponse,
}
import org.kote.repository._
import org.kote.service.UserService

class NotionUserService[F[_]: UUIDGen: MonadThrow: Clock](
    userRepository: UserRepository[F],
    boardRepository: BoardRepository[F],
    groupRepository: GroupRepository[F],
    taskRepository: TaskRepository[F],
    notionUserClient: NotionUserClient[F],
    notionPageClient: NotionPageClient[F],
    userToNotionUserIntegration: IntegrationRepository[F, UserId, NotionUserId],
    userMainPageIntegration: IntegrationRepository[F, UserId, NotionPageId],
) extends UserService[F] {

  override def create(createUser: CreateUser): OptionT[F, UnsafeUserResponse] =
    for {
      uuid <- OptionT.liftF(UUIDGen[F].randomUUID)
      date <- OptionT.liftF(Clock[F].realTimeInstant)
      user = User.fromCreateUser(uuid, date, createUser)
      _ <- OptionT.liftF(userRepository.create(user))
    } yield user.toUnsafeResponse()

  override def list: F[List[UserResponse]] =
    userRepository.all.map(_.map(_.toResponse))

  override def get(id: UserId): OptionT[F, UserResponse] =
    userRepository.get(id).map(_.toResponse)

  override def unsafeGet(id: UserId): OptionT[F, UnsafeUserResponse] =
    userRepository
      .get(id)
      .flatMap(user =>
        OptionT.liftF(for {
          optionNotionUserId <- userToNotionUserIntegration.getByKey(id).value
          notionUser <- optionNotionUserId
            .traverse(notionUserId => notionUserClient.get(notionUserId).value)
            .map(_.flatten)
        } yield user.toUnsafeResponse(notionUser)),
      )

  override def delete(id: UserId): OptionT[F, UnsafeUserResponse] =
    for {
      deleted <- userRepository.delete(id)
      boards <- boardRepository.list(deleted.id)
      _ <- boards.traverse(board => boardRepository.delete(board.id))
      groups <- boards.traverse(board => groupRepository.list(board.id)).map(_.flatten)
      _ <- groups.traverse(group => groupRepository.delete(group.id))
      tasks <- boards.traverse(board => taskRepository.listByBoard(board.id)).map(_.flatten)
      _ <- tasks.traverse(task => taskRepository.delete(task.id))
      notionUser <- // что-то может пойти не так, мы хотим избежать проблем в этом случае
        OptionT.liftF((for {
          notionUserId <- userToNotionUserIntegration.getByKey(id)
          notionUser <- notionUserClient.get(notionUserId)
        } yield notionUser).value)
      // Но даже если в предыдущем шаге что-то пошло не так, это мы должны удалить 100%
      _ <- OptionT.liftF(userToNotionUserIntegration.delete(id).value)
      _ <- OptionT.liftF(userMainPageIntegration.delete(id).value)
    } yield deleted.toUnsafeResponse(notionUser)

  override def linkToExternalUser(
      id: UserId,
      externalUser: ExternalUser,
  ): F[Option[UnsafeUserResponse]] = externalUser match {
    case NotionUser(optionNotionUserId, optionNotionName) =>
      (for {
        user <- userRepository.get(id)
        isEmpty <- OptionT.liftF(userToNotionUserIntegration.getByKey(id).isEmpty)
        notionUser <-
          if (isEmpty)
            for {
              // Сначала пытаемся добавить главную страницу,
              // потому что проверка идёт на последнее условие
              notionPages <- notionPageClient.search(PageSearchRequest(None, None))
              main <- OptionT.fromOption(notionPages.find(_.parent.isInstanceOf[WorkspaceParent]))
              _ <- OptionT.liftF(userMainPageIntegration.set(user.id, main.id))

              // А уже потом ищем пользователя
              notionUsers <- notionUserClient.list
              notionUser <- OptionT.fromOption(
                optionNotionUserId
                  .map(notionUserId => notionUsers.filter(_.id == notionUserId))
                  .orElse(
                    optionNotionName.map(notionName =>
                      notionUsers.filter(_.name.exists(_ == notionName)),
                    ),
                  )
                  .flatMap(_.headOption),
              )
              _ <- OptionT.liftF(userToNotionUserIntegration.set(id, notionUser.id))
            } yield notionUser
          else
            for {
              notionUserId <- userToNotionUserIntegration.getByKey(id)
              notionUser <- notionUserClient.get(notionUserId)
            } yield notionUser

      } yield user.toUnsafeResponse(Some(notionUser))).value
  }
}
