package org.kote.service

import cats.FlatMap
import cats.data.OptionT
import cats.effect.std.UUIDGen
import org.kote.domain.board.Board.BoardId
import org.kote.domain.board.{Board, BoardResponse, CreateBoard}
import org.kote.domain.user.User.UserId
import org.kote.repository.BoardRepository
import org.kote.repository.BoardRepository.BoardUpdateCommand
import cats.syntax.flatMap._
import cats.syntax.functor._

trait BoardService[F[_]] {
  def create(
      createBoard: CreateBoard,
  ): F[BoardResponse]

  def list(user: UserId): F[List[BoardResponse]]

  def get(id: BoardId): OptionT[F, BoardResponse]

  def update(id: BoardId, cmds: List[BoardUpdateCommand]): OptionT[F, BoardResponse]

  def delete(id: BoardId): OptionT[F, BoardResponse]
}

final case class RepositoryBoardService[F[_]: UUIDGen: FlatMap](
    boardRepository: BoardRepository[F],
) extends BoardService[F] {
  override def create(createBoard: CreateBoard): F[BoardResponse] =
    for {
      uuid <- UUIDGen[F].randomUUID
      board = Board.fromCreateBoard(uuid, createBoard)
      _ <- boardRepository.create(board)
    } yield board.toResponse

  override def list(user: UserId): F[List[BoardResponse]] =
    boardRepository.list.map(_.map(_.toResponse))

  override def get(id: BoardId): OptionT[F, BoardResponse] =
    boardRepository.get(id).map(_.toResponse)

  override def update(id: BoardId, cmds: List[BoardUpdateCommand]): OptionT[F, BoardResponse] =
    boardRepository.update(id, cmds).map(_.toResponse)

  override def delete(id: BoardId): OptionT[F, BoardResponse] =
    boardRepository.delete(id).map(_.toResponse)
}
