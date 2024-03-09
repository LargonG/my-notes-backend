package org.kote.repository.notion

import cats.Monad
import cats.data.OptionT
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapter, ToAdapter}
import org.kote.client.notion._
import org.kote.domain.group.Group
import org.kote.domain.group.Group.GroupId
import org.kote.repository.GroupRepository
import org.kote.repository.GroupRepository.GroupUpdateCommand

case class NotionGroupRepository[F[_]: Monad](
    pageClient: NotionPageClient[F],
    dbClient: NotionDatabaseClient[F],
)(implicit
    val groupAdapter: Adapter[Group, NotionDatabasePropertiesUpdateRequest, NotionDatabaseResponse],
    val groupIdAdapter: Adapter[GroupId, NotionDatabaseId, NotionDatabaseId],
) extends GroupRepository[F] {
  override def create(obj: Group): F[Long] =
    (for {
      _ <- dbClient.update(obj.id.toRequest, obj.toRequest)
    } yield 1L).getOrElse(0L)

  // нет
  override def list: F[List[Group]] = ???

  override def get(id: GroupId): OptionT[F, Group] =
    for {
      response <- dbClient.get(id.toRequest)
    } yield response.fromResponse

  /** Notion не предоставляет способа удалить данные...
    * @param id
    * @return
    */
  override def delete(id: GroupId): OptionT[F, Group] = OptionT.none[F, Group]

  // Опять-таки, что-то сложное
  override def update(
      id: GroupId,
      cmds: List[GroupUpdateCommand],
  ): OptionT[F, Group] = ???
}
