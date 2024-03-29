package org.kote.repository

import cats.data.OptionT

trait Repository[F[_], T, ID] {
  def create(obj: T): F[Long]

  def get(id: ID): OptionT[F, T]

  def delete(id: ID): OptionT[F, T]
}
