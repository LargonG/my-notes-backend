package org.kote.repository.postgresql

import cats.data.OptionT
import cats.effect.kernel.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import org.kote.domain.user.User
import org.kote.repository.UserRepository

class UserRepositoryPostgresql[F[_]: MonadCancelThrow](implicit val tr: Transactor[F])
    extends UserRepository[F]
    with QuillInstances {

  private final val name = "\"user\""

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def create(user: User): F[Long] = run {
    quote {
      querySchema[User](name).insertValue(lift(user))
    }
  }.transact(tr)

  override def get(id: User.UserId): OptionT[F, User] = OptionT(run {
    quote {
      querySchema[User](name).filter(_.id == lift(id))
    }
  }.transact(tr).map(_.headOption))

  override def all: F[List[User]] = run {
    quote {
      querySchema[User](name)
    }
  }.transact(tr)

  override def delete(id: User.UserId): OptionT[F, User] = OptionT(run {
    quote {
      querySchema[User](name).filter(_.id == lift(id)).delete.returningMany(r => r)
    }
  }.transact(tr).map(_.headOption))

  override def update(id: User.UserId, cmds: UserRepository.UserUpdateCommand*): OptionT[F, User] =
    (for {
      user <- OptionT(
        run {
          quote {
            querySchema[User](name).filter(_.id == lift(id))
          }
        }.map(_.headOption),
      )
      newUser = cmds.foldLeft(user)(UserRepository.standardUpdateLoop)
      _ <- OptionT.liftF(run {
        quote {
          querySchema[User](name).updateValue(lift(newUser))
        }
      })
    } yield newUser).transact(tr)
}
