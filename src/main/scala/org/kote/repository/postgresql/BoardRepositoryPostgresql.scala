package org.kote.repository.postgresql

import cats.data.OptionT
import cats.effect.kernel.MonadCancelThrow
import cats.implicits.toFunctorOps
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import org.kote.domain.board.Board
import org.kote.domain.user.User
import org.kote.repository.BoardRepository
import doobie.Transactor
import doobie.implicits._

class BoardRepositoryPostgresql[F[_]: MonadCancelThrow](implicit tr: Transactor[F])
    extends BoardRepository[F]
    with QuillInstances {

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def create(board: Board): F[Long] = run {
    quote {
      query[Board].insertValue(lift(board))
    }
  }.transact(tr)

  override def get(id: Board.BoardId): OptionT[F, Board] = OptionT(run {
    quote {
      query[Board].filter(_.id == lift(id))
    }
  }.transact(tr).map(_.headOption))

  override def all: F[List[Board]] = run {
    quote {
      query[Board]
    }
  }.transact(tr)

  override def list(userId: User.UserId): OptionT[F, List[Board]] = OptionT.liftF(run {
    quote {
      query[Board].filter(_.owner == lift(userId))
    }
  }.transact(tr))

  override def update(
      id: Board.BoardId,
      cmds: BoardRepository.BoardUpdateCommand*,
  ): OptionT[F, Board] =
    (for {
      board <- OptionT(
        run {
          quote {
            query[Board].filter(_.id == lift(id))
          }
        }.map(_.headOption),
      )
      newBoard = cmds.foldLeft(board)(BoardRepository.standardUpdateLoop)
      _ <- OptionT.liftF(
        run {
          quote {
            query[Board].filter(_.id == lift(id)).updateValue(lift(newBoard))
          }
        },
      )
    } yield newBoard).transact(tr)

  override def delete(id: Board.BoardId): OptionT[F, Board] = OptionT(run {
    quote {
      query[Board].filter(_.id == lift(id)).delete.returningMany(r => r)
    }
  }.transact(tr).map(_.headOption))
}
