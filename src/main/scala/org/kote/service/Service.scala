package org.kote.service

import cats.data.OptionT

trait Service[F[_], IN, OUT, ID] {
  def create(obj: IN): F[OUT]

  def list: F[List[OUT]]

  def get(id: ID): OptionT[F, OUT]

  def delete(id: ID): OptionT[F, OUT]
}
