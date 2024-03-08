package org.kote.repository

import cats.{Applicative, Monad}
import org.kote.adapter.Adapter
import org.kote.client.notion.NotionDatabaseClient
import org.kote.client.notion.model.database.{DbId, DbRequest, DbResponse}
import org.kote.common.cache.Cache
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.repository.BoardRepository.BoardUpdateCommand
import org.kote.repository.inmemory.InMemoryBoardRepository
import org.kote.repository.notion.NotionBoardRepository

trait BoardRepository[F[_]] extends UpdatableRepository[F, Board, BoardId, BoardUpdateCommand] {}

object BoardRepository {
  sealed trait BoardUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends BoardUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, BoardId, Board]): BoardRepository[F] =
    new InMemoryBoardRepository[F](cache)

  def notion[F[_]: Applicative](
      client: NotionDatabaseClient[F],
  )(implicit
      boardAdapter: Adapter[Board, DbRequest, DbResponse],
      idAdapter: Adapter[BoardId, DbId, DbId],
  ) = new NotionBoardRepository[F](client)
}
