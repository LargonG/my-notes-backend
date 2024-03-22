package org.kote.common.parser

import cats.data.Reader

trait Parser[E, K, T] {
  def parse(input: LazyList[String]): Reader[K, Either[E, T]]
}

object Parser {
  def apply[E, K, T: Parser[E, K, *]]: Parser[E, K, T] = implicitly
}
