package org.kote.client.notion.utils

trait Query[T] {
  def toQuery(value: T): String
}

object Query {
  def apply[T: Query]: Query[T] = implicitly

  implicit final class ToQuery[T: Query](value: T) {
    def toQuery: String = Query[T].toQuery(value)
  }
}
