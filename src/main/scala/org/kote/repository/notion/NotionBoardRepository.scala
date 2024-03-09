package org.kote.repository.notion

import cats.Applicative
import cats.data.OptionT
import cats.syntax.functor._
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapter, ToAdapter}
import org.kote.client.notion.{
  NotionDatabaseClient,
  NotionDatabaseCreateRequest,
  NotionDatabaseId,
  NotionDatabaseResponse,
}
import org.kote.domain.board.Board
import org.kote.domain.board.Board.BoardId
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
    } yield response.fromResponse).as(1L)

  // нет
  override def list: F[List[Board]] = ???

  override def get(id: BoardId): OptionT[F, Board] =
    for {
      response <- client.get(id.toRequest)
    } yield response.fromResponse

  /** Notion api не позволяет удалить базу данных, поэтому тут ничего не происходит
    * @param id
    *   таблицы
    * @return
    *   None
    */
  override def delete(id: BoardId): OptionT[F, Board] = OptionT.none[F, Board]

  override def update(
      id: Board.BoardId,
      cmds: List[BoardRepository.BoardUpdateCommand],
  ): OptionT[F, Board] = ???
}
