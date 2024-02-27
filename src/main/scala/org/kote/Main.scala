package org.kote

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO.println("Hello, world")
    } yield ExitCode.Success
}
