package org.kote.service

import cats.Monad
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.implicits.toTraverseOps
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.client.notion.{NotionDatabaseClient, NotionDatabaseId, NotionPageId, NotionUserId}
import org.kote.domain.board.Board.BoardId
import org.kote.domain.board.{Board, BoardResponse, CreateBoard}
import org.kote.domain.user.User.UserId
import org.kote.repository.{BoardRepository, GroupRepository, IntegrationRepository, TaskRepository}
import org.kote.service.notion.v1.NotionBoardService

trait BoardService[F[_]] {
  def create(
      createBoard: CreateBoard,
  ): OptionT[F, BoardResponse]

  def list(user: UserId): F[List[BoardResponse]]

  def get(id: BoardId): OptionT[F, BoardResponse]

  def delete(id: BoardId): OptionT[F, BoardResponse]
}

object BoardService {
  def fromRepository[F[_]: UUIDGen: Monad](
      boardRepository: BoardRepository[F],
      groupRepository: GroupRepository[F],
      taskRepository: TaskRepository[F],
  ): BoardService[F] =
    new RepositoryBoardService[F](boardRepository, groupRepository, taskRepository)

  def syncNotion[F[_]: UUIDGen: Monad](
      boardRepository: BoardRepository[F],
      groupRepository: GroupRepository[F],
      taskRepository: TaskRepository[F],
      notionDatabaseClient: NotionDatabaseClient[F],
      databaseIntegration: IntegrationRepository[F, BoardId, NotionDatabaseId],
      userMainPageIntegration: IntegrationRepository[F, UserId, NotionPageId],
      userToUserIntegration: IntegrationRepository[F, UserId, NotionUserId],
  ): BoardService[F] =
    new NotionBoardService[F](
      boardRepository,
      groupRepository,
      taskRepository,
      notionDatabaseClient,
      databaseIntegration,
      userMainPageIntegration,
      userToUserIntegration,
    )
}

class RepositoryBoardService[F[_]: UUIDGen: Monad](
    boardRepository: BoardRepository[F],
    groupRepository: GroupRepository[F],
    taskRepository: TaskRepository[F],
) extends BoardService[F] {
  override def create(createBoard: CreateBoard): OptionT[F, BoardResponse] =
    OptionT.liftF(for {
      uuid <- UUIDGen[F].randomUUID
      board = Board.fromCreateBoard(uuid, createBoard)
      _ <- boardRepository.create(board)
    } yield board.toResponse)

  override def list(user: UserId): F[List[BoardResponse]] =
    boardRepository.list(user).map(_.map(_.toResponse)).getOrElse(List())

  override def get(id: BoardId): OptionT[F, BoardResponse] =
    boardRepository.get(id).map(_.toResponse)

  override def delete(id: BoardId): OptionT[F, BoardResponse] =
    for {
      deleted <- boardRepository.delete(id)
      groups <- groupRepository.list(id)
      _ <- groups.traverse(group => groupRepository.delete(group.id))
      tasks <- taskRepository.listByBoard(id)
      _ <- tasks.traverse(task => taskRepository.delete(task.id))
    } yield deleted.toResponse
}
