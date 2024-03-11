package org.kote.service

import cats.Monad
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.implicits.toTraverseOps
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.domain.board.Board.BoardId
import org.kote.domain.board.{Board, BoardResponse, CreateBoard}
import org.kote.domain.user.User.UserId
import org.kote.repository.BoardRepository.BoardUpdateCommand
import org.kote.repository.{BoardRepository, GroupRepository, TaskRepository}

trait BoardService[F[_]] {
  def create(
      createBoard: CreateBoard,
  ): F[BoardResponse]

  def list(user: UserId): F[List[BoardResponse]]

  def get(id: BoardId): OptionT[F, BoardResponse]

  def update(id: BoardId, cmds: List[BoardUpdateCommand]): OptionT[F, BoardResponse]

  def delete(id: BoardId): OptionT[F, BoardResponse]
}

object BoardService {
  def fromRepository[F[_]: UUIDGen: Monad](
      boardRepository: BoardRepository[F],
      groupRepository: GroupRepository[F],
      taskRepository: TaskRepository[F],
  ): BoardService[F] =
    new RepositoryBoardService[F](boardRepository, groupRepository, taskRepository)
}

class RepositoryBoardService[F[_]: UUIDGen: Monad](
    boardRepository: BoardRepository[F],
    groupRepository: GroupRepository[F],
    taskRepository: TaskRepository[F],
) extends BoardService[F] {
  override def create(createBoard: CreateBoard): F[BoardResponse] =
    for {
      uuid <- UUIDGen[F].randomUUID
      board = Board.fromCreateBoard(uuid, createBoard)
      _ <- boardRepository.create(board)
    } yield board.toResponse

  override def list(user: UserId): F[List[BoardResponse]] =
    boardRepository.list(user).map(_.map(_.toResponse)).getOrElse(List())

  override def get(id: BoardId): OptionT[F, BoardResponse] =
    boardRepository.get(id).map(_.toResponse)

  override def update(id: BoardId, cmds: List[BoardUpdateCommand]): OptionT[F, BoardResponse] =
    boardRepository.update(id, cmds).map(_.toResponse)

  override def delete(id: BoardId): OptionT[F, BoardResponse] =
    for {
      deleted <- boardRepository.delete(id)
      groups <- deleted.groups.traverse(groupRepository.delete)
      _ <- groups.traverse(_.tasks.traverse(taskRepository.delete))
    } yield deleted.toResponse
}
