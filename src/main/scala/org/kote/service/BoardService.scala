package org.kote.service

import cats.FlatMap
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.domain.board.Board.BoardId
import org.kote.domain.board.{Board, BoardResponse, CreateBoard}
import org.kote.domain.user.User.UserId
import org.kote.repository.BoardRepository
import org.kote.repository.BoardRepository.BoardUpdateCommand

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
  def fromRepository[F[_]: UUIDGen: FlatMap](
      boardRepository: BoardRepository[F],
  ): BoardService[F] =
    new RepositoryBoardService[F](boardRepository)
}

class RepositoryBoardService[F[_]: UUIDGen: FlatMap](
    boardRepository: BoardRepository[F],
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
    boardRepository.delete(id).map(_.toResponse)
}
