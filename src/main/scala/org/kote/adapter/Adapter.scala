package org.kote.adapter

import cats.Functor
import cats.implicits.toFunctorOps

trait Adapter[Type, Request, Response] {
  def toRequest(value: Type): Request
  def fromResponse(response: Response): Type
}

object Adapter {
  def apply[T, Req, Res](implicit adapter: Adapter[T, Req, Res]): Adapter[T, Req, Res] = adapter

  implicit final class ToAdapter[T, Req, Res](value: T)(implicit
      val adapter: Adapter[T, Req, Res],
  ) {
    def toRequest: Req = adapter.toRequest(value)
  }

  implicit final class ToAdapterF[F[_]: Functor, T, Req, Res](value: F[T])(implicit
      val adapter: Adapter[T, Req, Res],
  ) {
    def toRequest: F[Req] = value.map(_.toRequest)
  }

  implicit final class FromAdapter[T, Req, Res](response: Res)(implicit
      val adapter: Adapter[T, Req, Res],
  ) {
    def fromResponse: T = adapter.fromResponse(response)
  }
  implicit final class FromAdapterF[F[_]: Functor, T, Req, Res](response: F[Res])(implicit
      val adapter: Adapter[T, Req, Res],
  ) {
    def fromResponse: F[T] = response.map(_.fromResponse)
  }
}
