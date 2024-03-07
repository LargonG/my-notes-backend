package org.kote.client.notion.utils

trait Typed[V, T] {
  def getType(value: V): T
}

object Typed {
  def apply[V: Typed[*, T], T]: Typed[V, T] = implicitly

  implicit final class ToTyped[V: Typed[*, T], T](value: V) {
    def toType: T = Typed[V, T].getType(value)
  }
}
