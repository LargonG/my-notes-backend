package org.kote.repository.postgresql

import cats.data.OptionT
import cats.effect.kernel.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.util.transactor.Transactor
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import org.kote.domain.board.Board
import org.kote.domain.group.Group
import org.kote.repository.GroupRepository
import doobie.implicits._

class GroupRepositoryPostgresql[F[_]: MonadCancelThrow](implicit tr: Transactor[F])
    extends GroupRepository[F] {

  private final val name = "\"group\""

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def create(group: Group): F[Long] = run {
    quote {
      querySchema[Group](name).insertValue(lift(group))
    }
  }.transact(tr)

  override def get(id: Group.GroupId): OptionT[F, Group] = OptionT(run {
    quote {
      querySchema[Group](name).filter(_.id == lift(id))
    }
  }.transact(tr).map(_.headOption))

  override def list(boardId: Board.BoardId): OptionT[F, List[Group]] = OptionT.liftF(run {
    quote {
      querySchema[Group](name).filter(_.boardId == lift(boardId))
    }
  }.transact(tr))

  override def delete(id: Group.GroupId): OptionT[F, Group] = OptionT(run {
    quote {
      querySchema[Group](name).filter(_.id == lift(id)).delete.returningMany(r => r)
    }
  }.transact(tr).map(_.headOption))

  override def update(
      id: Group.GroupId,
      cmds: GroupRepository.GroupUpdateCommand*,
  ): OptionT[F, Group] =
    (for {
      group <- OptionT(run {
        quote {
          querySchema[Group](name).filter(_.id == lift(id))
        }
      }.map(_.headOption))
      newGroup = cmds.foldLeft(group)(GroupRepository.standardUpdateGroup)
      _ <- OptionT.liftF(run {
        quote {
          querySchema[Group](name).filter(_.id == lift(id)).updateValue(lift(newGroup))
        }
      })
    } yield newGroup).transact(tr)
}
