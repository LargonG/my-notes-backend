package org.kote.service.notion.v1

import cats.Monad
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.implicits.{toFlatMapOps, toTraverseOps}
import cats.syntax.functor._
import org.kote.client.notion._
import org.kote.client.notion.model.database.DbSearchRequest
import org.kote.domain.board.Board.BoardId
import org.kote.domain.board.{Board, BoardResponse, CreateBoard}
import org.kote.domain.user.User
import org.kote.domain.user.User.UserId
import org.kote.repository.{BoardRepository, GroupRepository, IntegrationRepository, TaskRepository}
import org.kote.service.BoardService

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
    userToUserIntegration: IntegrationRepository[F, UserId, NotionUserId],
) extends BoardService[F] {

  override def create(createBoard: CreateBoard): OptionT[F, BoardResponse] =
    OptionT(
      (for {
        uuid <- UUIDGen[F].randomUUID
        board = Board.fromCreateBoard(uuid, createBoard)
        _ <- boardRepository.create(board)
        result <- (for {
          mainPage <- userMainPageIntegration.getByKey(createBoard.createdBy)
          response <- notionDatabaseClient.create(
            CreateBoard.toNotionRequest(createBoard, mainPage),
          )
          _ <- OptionT.liftF(databaseIntegration.set(board.id, response.id))
        } yield response).value
      } yield BoardResponse
        .fromNotionResponse(result)(
          databaseIntegration,
          userToUserIntegration,
        )
        .value).flatten,
    )

  override def list(user: User.UserId): F[List[BoardResponse]] =
    (for {
      notionUserId <- userToUserIntegration.getByKey(user)
      databases <- notionDatabaseClient.search(DbSearchRequest(None, None))
      filtered <- OptionT.pure[F](
        databases.filter(response => response.createdBy.id == notionUserId),
      )
      mapped <- filtered.traverse(elem =>
        BoardResponse.fromNotionResponse(Some(elem))(
          databaseIntegration,
          userToUserIntegration,
        ),
      )
    } yield mapped).getOrElse(List.empty)

  override def get(id: Board.BoardId): OptionT[F, BoardResponse] =
    for {
      databaseId <- databaseIntegration.getByKey(id)
      database <- notionDatabaseClient.get(databaseId)
      res <- BoardResponse.fromNotionResponse(Some(database))(
        databaseIntegration,
        userToUserIntegration,
      )
    } yield res

  override def delete(id: Board.BoardId): OptionT[F, BoardResponse] =
    for {
      deleted <- boardRepository.delete(id)
      groups <- groupRepository.list(deleted.id)
      _ <- groups.traverse(group => groupRepository.delete(group.id))
      tasks <- taskRepository.listByBoard(deleted.id)
      _ <- tasks.traverse(task => taskRepository.delete(task.id))
      _ <- databaseIntegration.delete(deleted.id)
    } yield deleted.toResponse
}
