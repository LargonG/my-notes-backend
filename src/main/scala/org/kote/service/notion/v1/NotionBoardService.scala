package org.kote.service.notion.v1

import cats.Monad
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.implicits.{toFlatMapOps, toTraverseOps}
import cats.syntax.functor._
import org.kote.client.notion._
import org.kote.client.notion.model.database.request.{DatabaseSearchRequest, DatabaseUpdateRequest}
import org.kote.client.notion.model.text.RichText
import org.kote.domain.board.Board.BoardId
import org.kote.domain.board.{Board, BoardResponse, CreateBoard}
import org.kote.domain.group.GroupResponse
import org.kote.domain.user.User
import org.kote.domain.user.User.UserId
import org.kote.repository.{BoardRepository, GroupRepository, IntegrationRepository, TaskRepository}
import org.kote.service.BoardService

import scala.annotation.unused

class NotionBoardService[F[_]: Monad: UUIDGen](
    // repositories
    boardRepository: BoardRepository[F],
    groupRepository: GroupRepository[F],
    taskRepository: TaskRepository[F],
    // clients
    notionDatabaseClient: NotionDatabaseClient[F],
    // integrations
    databaseIntegration: IntegrationRepository[F, BoardId, NotionDatabaseId],
    userMainPageIntegration: IntegrationRepository[F, UserId, NotionPageId],
    @unused
    userToUserIntegration: IntegrationRepository[F, UserId, NotionUserId],
) extends BoardService[F] {

  override def create(createBoard: CreateBoard): OptionT[F, BoardResponse] =
    OptionT.liftF(
      for {
        uuid <- UUIDGen[F].randomUUID
        board = Board.fromCreateBoard(uuid, createBoard)
        _ <- boardRepository.create(board)
      } yield board.toResponse,
    )

  override def list(user: User.UserId): F[List[BoardResponse]] =
    (for {
      board <- boardRepository.list(user)
    } yield board.map(_.toResponse)).getOrElse(List.empty)

  override def get(id: Board.BoardId): OptionT[F, BoardResponse] =
    for {
      board <- boardRepository.get(id)
    } yield board.toResponse

  override def listGroups(id: BoardId): OptionT[F, List[GroupResponse]] =
    groupRepository.list(id).map(_.map(_.toResponse))

  override def delete(id: Board.BoardId): OptionT[F, BoardResponse] =
    for {
      deleted <- boardRepository.delete(id)
      groups <- groupRepository.list(deleted.id)
      _ <- groups.traverse(group => groupRepository.delete(group.id))
      tasks <- taskRepository.listByBoard(deleted.id)
      _ <- tasks.traverse(task => taskRepository.delete(task.id))
      _ <- databaseIntegration.delete(deleted.id)
    } yield deleted.toResponse

  override def importFromIntegration(userId: UserId): F[Option[List[BoardResponse]]] =
    (for {
      notionUser <- userToUserIntegration.getByKey(userId)
      databases <- notionDatabaseClient.search(DatabaseSearchRequest(None, None))
      madeByNotionUser = databases.filter(_.createdBy.id == notionUser)
      results <- madeByNotionUser.traverse(database =>
        for {
          isEmpty <- OptionT.liftF(databaseIntegration.getByValue(database.id).isEmpty)
          board <-
            if (isEmpty)
              for {
                board <- create(CreateBoard(database.title.mkString, userId))
                _ <- OptionT.liftF(databaseIntegration.set(board.id, database.id))
              } yield board
            else
              for {
                boardId <- databaseIntegration.getByValue(database.id)
                board <- boardRepository.get(boardId)
              } yield board.toResponse
        } yield board,
      )
    } yield results).value

  override def exportToIntegration(id: BoardId): F[Option[BoardResponse]] =
    (for {
      board <- boardRepository.get(id)
      mainPage <- userMainPageIntegration.getByKey(board.owner)
      isEmpty <- OptionT.liftF(databaseIntegration.getByKey(id).isEmpty)
      _ <-
        if (isEmpty) {
          for {
            response <- notionDatabaseClient.create(
              CreateBoard.toNotionRequest(CreateBoard(board.title, board.owner), mainPage),
            )
            _ <- OptionT.liftF(databaseIntegration.set(board.id, response.id))
          } yield response
        } else
          for {
            databaseId <- databaseIntegration.getByKey(id)
            _ <- notionDatabaseClient.update(
              databaseId,
              /*
              todo:
                если захотим добавить group service, то тут мы должны попробовать создать
                property для групп, если оно ещё не было создано, но так как у нас пока не предусмотрена
                работа notion с group, то этого нет
               */
              DatabaseUpdateRequest(Some(List(RichText.text(board.title))), Map.empty),
            )
            response <- notionDatabaseClient.get(databaseId)
          } yield response
    } yield board.toResponse).value

}
