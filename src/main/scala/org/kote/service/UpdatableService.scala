package org.kote.service

import cats.data.OptionT
trait UpdatableService[F[_], IN, OUT, ID, CMD] extends Service[F, IN, OUT, ID] {
  def update(id: ID, cmds: List[CMD]): OptionT[F, OUT]
}
