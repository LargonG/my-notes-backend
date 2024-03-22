package org.kote.common.adapter

trait Adapter[K, V] {
  def get(from: K): V
}

object Adapter {
  def apply[K, V](implicit adapter: Adapter[K, V]): Adapter[K, V] =
    implicitly

  implicit class FromAdapter[K, V](key: K)(implicit val adapter: Adapter[K, V]) {
    def adapt: V = adapter.get(key)
  }
}
