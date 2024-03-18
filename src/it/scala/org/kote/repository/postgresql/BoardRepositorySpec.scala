package org.kote.repository.postgresql

import cats.effect.testing.scalatest.{AsyncIOSpec, CatsResourceIO}
import cats.effect.{IO, Resource}
import cats.implicits.catsSyntaxOptionId
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.Transactor
import org.kote.config.PostgresConfig
import org.kote.database.FlywayMigration
import org.kote.database.transactor.makeTransactor
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User.UserId
import org.scalatest.flatspec.FixtureAsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.containers.wait.strategy.{
  LogMessageWaitStrategy,
  Wait,
  WaitAllStrategy,
  WaitStrategy,
}
import org.testcontainers.utility.DockerImageName

import java.util.UUID

class BoardRepositorySpec
    extends FixtureAsyncFlatSpec
    with AsyncIOSpec
    with CatsResourceIO[Transactor[IO]]
    with Matchers {
  override val resource: Resource[IO, Transactor[IO]] =
    for {
      c <- containerResource
      conf = PostgresConfig(
        c.jdbcUrl,
        user = c.username,
        password = c.password,
        poolSize = 2,
      )
      _ <- Resource.eval(FlywayMigration.migrate[IO](conf))
      tx <- makeTransactor[IO](conf)
    } yield tx

  "BoardRepositoryPostgresql" should "return empty list if db is empty" in { implicit t =>
    val repo = new BoardRepositoryPostgresql[IO]
    val boardId = BoardId(UUID.randomUUID())

    for {
      _ <- repo.all.asserting(_ shouldBe List.empty)
      _ <- repo.get(boardId).value.asserting(_ shouldBe None)
      _ <- repo.delete(boardId).value.asserting(_ shouldBe None)
    } yield ()
  }

  it should "insert new board" in { implicit t =>
    val repo = new BoardRepositoryPostgresql[IO]
    val board = Board(BoardId(UUID.randomUUID()), "title", UserId(UUID.randomUUID()))

    for {
      _ <- repo.create(board).asserting(_ shouldBe 1L)
      _ <- repo.get(board.id).value.asserting(_ shouldBe board.some)
      _ <- repo.list(board.owner).value.asserting(_ shouldBe List(board).some)
      _ <- repo.all.asserting(_ shouldBe List(board))
      _ <- repo.delete(board.id).value.asserting(_ shouldBe board.some)
    } yield ()
  }

  private val defaultWaitStrategy: WaitStrategy = new WaitAllStrategy()
    .withStrategy(Wait.forListeningPort())
    .withStrategy(
      new LogMessageWaitStrategy()
        .withRegEx(".*database system is ready to accept connections.*\\s")
        .withTimes(2),
    )

  private def containerResource: Resource[IO, PostgreSQLContainer] =
    Resource.make(
      IO {
        val c = PostgreSQLContainer
          .Def(
            dockerImageName = DockerImageName.parse("postgres:14.7"),
          )
          .start()
        c.container.waitingFor(defaultWaitStrategy)
        c
      },
    )(c => IO(c.stop()))
}
