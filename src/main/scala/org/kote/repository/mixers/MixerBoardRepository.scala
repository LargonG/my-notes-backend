package org.kote.repository.mixers

import cats.data.{NonEmptyList, OptionT}
import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import org.kote.domain.board.Board
import org.kote.domain.user.User
import org.kote.repository.BoardRepository

class MixerBoardRepository[F[_]: Async](
    repositories: NonEmptyList[BoardRepository[F]],
) extends BoardRepository[F] {
  override def all: F[List[Board]] =
    repositories
      .map(repo => Async[F].executionContext.flatMap(ec => Async[F].evalOn(repo.all, ec)))
      .head

  override def list(userId: User.UserId): OptionT[F, List[Board]] =
    OptionT(
      repositories
        .map(repo =>
          Async[F].executionContext.flatMap(ec => Async[F].evalOn(repo.list(userId).value, ec)),
        )
        .head,
    )

  override def update(
      id: Board.BoardId,
      cmds: List[BoardRepository.BoardUpdateCommand],
  ): OptionT[F, Board] =
    OptionT(
      repositories
        .map(repo =>
          Async[F].executionContext.flatMap(ec => Async[F].evalOn(repo.update(id, cmds).value, ec)),
        )
        .head,
    )

  override def create(obj: Board): F[Long] =
    repositories
      .map(repo => Async[F].executionContext.flatMap(ec => Async[F].evalOn(repo.create(obj), ec)))
      .head

  override def get(id: Board.BoardId): OptionT[F, Board] =
    OptionT(
      repositories
        .map(repo =>
          Async[F].executionContext.flatMap(ec => Async[F].evalOn(repo.get(id).value, ec)),
        )
        .head,
    )

  override def delete(id: Board.BoardId): OptionT[F, Board] =
    OptionT(
      repositories
        .map(repo =>
          Async[F].executionContext.flatMap(ec => Async[F].evalOn(repo.delete(id).value, ec)),
        )
        .head,
    )
}
