package org.kote.common.parser

trait Parser[E, T] {
  def parse(input: LazyList[String]): Either[E, T]
}

object Parser {
  def apply[E, T: Parser[E, *]]: Parser[E, T] = implicitly
}
