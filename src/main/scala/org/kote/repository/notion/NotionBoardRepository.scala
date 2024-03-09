package org.kote.repository.notion

import cats.Applicative
import cats.data.OptionT
import cats.syntax.functor._
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapter, FromAdapterF, ToAdapter}
import org.kote.client.notion.model.database.DbSearchRequest
import org.kote.client.notion.{
  NotionDatabaseClient,
  NotionDatabaseCreateRequest,
  NotionDatabaseId,
  NotionDatabaseResponse,
}
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User
import org.kote.repository.BoardRepository

case class NotionBoardRepository[F[_]: Applicative](
    client: NotionDatabaseClient[F],
)(implicit
    val boardAdapter: Adapter[Board, NotionDatabaseCreateRequest, NotionDatabaseResponse],
    val idAdapter: Adapter[BoardId, NotionDatabaseId, NotionDatabaseId],
) extends BoardRepository[F] {
  override def create(obj: Board): F[Long] =
    (for {
      response <- client.create(obj.toRequest)
    } yield response.fromResponse).as(1L).getOrElse(0L)

  override def all: F[List[Board]] =
    client
      .search(DbSearchRequest(None, None))
      .map(_.fromResponse)
      .getOrElse(List())

  override def list(userId: User.UserId): OptionT[F, List[Board]] =
    client
      .search(DbSearchRequest(None, None))
      .map(_.fromResponse)
      .map(_.filter(_.owner == userId))

  override def get(id: BoardId): OptionT[F, Board] =
    for {
      response <- client.get(id.toRequest)
    } yield response.fromResponse

  /** Notion api не позволяет удалить базу данных, поэтому просто получим таблицу и всё
    * @param id
    *   таблицы
    * @return
    *   вызывает [[get]]
    */
  override def delete(id: BoardId): OptionT[F, Board] = get(id)

  override def update(
      id: Board.BoardId,
      cmds: List[BoardRepository.BoardUpdateCommand],
  ): OptionT[F, Board] = ???

}
