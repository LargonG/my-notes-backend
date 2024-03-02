package org.kote.service

import cats.FlatMap
import cats.data.OptionT
import cats.effect.kernel.Clock
import cats.effect.std.UUIDGen
import org.kote.domain.user.User.UserId
import org.kote.domain.user.{CreateUser, UnsafeUserResponse, User, UserResponse}
import org.kote.repository.UserRepository
import org.kote.repository.UserRepository.UserUpdateCommand

import cats.syntax.flatMap._
import cats.syntax.functor._

trait UserService[F[_]] {
  def create(createUser: CreateUser): F[UnsafeUserResponse]

  def update(id: UserId, cmds: List[UserUpdateCommand]): OptionT[F, UnsafeUserResponse]

  def list: F[List[UserResponse]]

  def get(id: UserId): OptionT[F, UserResponse]

  def unsafeGet(id: UserId): OptionT[F, UnsafeUserResponse]

  def delete(id: UserId): OptionT[F, UnsafeUserResponse]
}

final case class RepositoryUserService[F[_]: UUIDGen: FlatMap: Clock](
    userRepository: UserRepository[F],
) extends UserService[F] {
  override def create(createUser: CreateUser): F[UnsafeUserResponse] =
    for {
      uuid <- UUIDGen[F].randomUUID
      date <- Clock[F].realTimeInstant
      user = User.fromCreateUser(uuid, date, createUser)
      _ <- userRepository.create(user)
    } yield user.toUnsafeResponse

  override def update(id: UserId, cmds: List[UserUpdateCommand]): OptionT[F, UnsafeUserResponse] =
    userRepository.update(id, cmds).map(_.toUnsafeResponse)

  override def list: F[List[UserResponse]] =
    userRepository.list.map(_.map(_.toResponse))

  override def get(id: UserId): OptionT[F, UserResponse] =
    userRepository.get(id).map(_.toResponse)

  override def unsafeGet(id: UserId): OptionT[F, UnsafeUserResponse] =
    userRepository.get(id).map(_.toUnsafeResponse)

  override def delete(id: UserId): OptionT[F, UnsafeUserResponse] =
    userRepository.delete(id).map(_.toUnsafeResponse)
}
