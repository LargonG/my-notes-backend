package org.kote.repository

import cats.data.OptionT
import cats.{Applicative, Monad}
import org.kote.adapter.Adapter
import org.kote.client.notion.{
  NotionDatabaseClient,
  NotionDatabaseCreateRequest,
  NotionDatabaseId,
  NotionDatabaseResponse,
}
import org.kote.common.cache.Cache
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User.UserId
import org.kote.repository.BoardRepository.BoardUpdateCommand
import org.kote.repository.inmemory.InMemoryBoardRepository
import org.kote.repository.notion.NotionBoardRepository

trait BoardRepository[F[_]] extends UpdatableRepository[F, Board, BoardId, BoardUpdateCommand] {
  def all: F[List[Board]]

  def list(userId: UserId): OptionT[F, List[Board]]
}

object BoardRepository {
  sealed trait BoardUpdateCommand extends UpdateCommand

  final case class UpdateTitle(title: String) extends BoardUpdateCommand

  def inMemory[F[_]: Monad](cache: Cache[F, BoardId, Board]): BoardRepository[F] =
    new InMemoryBoardRepository[F](cache)

  def notion[F[_]: Applicative](
      client: NotionDatabaseClient[F],
  )(implicit
      boardAdapter: Adapter[Board, NotionDatabaseCreateRequest, NotionDatabaseResponse],
      idAdapter: Adapter[BoardId, NotionDatabaseId, NotionDatabaseId],
  ) = new NotionBoardRepository[F](client)
}
