package org.kote.service.notion.v1

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.kernel.Clock
import cats.effect.std.UUIDGen
import cats.implicits.toTraverseOps
import cats.syntax.functor._
import org.kote.client.notion.model.page.PageSearchRequest
import org.kote.client.notion.model.parent.WorkspaceParent
import org.kote.client.notion.{NotionPageClient, NotionPageId, NotionUserClient, NotionUserId}
import org.kote.domain.user.User.UserId
import org.kote.domain.user.{CreateUser, UnsafeUserResponse, User, UserResponse}
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

      notionUsers <- notionUserClient.list
      notionUser <- OptionT.fromOption(
        notionUsers.find(_.name.getOrElse("") == createUser.notionUserName),
      )
      _ <- OptionT.liftF(userToNotionUserIntegration.set(user.id, notionUser.id))

      notionPages <- notionPageClient.search(PageSearchRequest(None, None))
      main <- OptionT.fromOption(notionPages.find(_.parent.isInstanceOf[WorkspaceParent]))
      _ <- OptionT.liftF(userMainPageIntegration.set(user.id, main.id))
    } yield user.toUnsafeResponse(Option(notionUser))

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
      groups <- boards.traverse(board => groupRepository.list(board.id)).map(_.flatten)
      _ <- groups.traverse(group => groupRepository.delete(group.id))
      tasks <- boards.traverse(board => taskRepository.listByBoard(board.id)).map(_.flatten)
      _ <- tasks.traverse(task => taskRepository.delete(task.id))
    } yield deleted.toUnsafeResponse()
}
