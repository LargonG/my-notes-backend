package org.kote.repository.notion

import cats.Monad
import cats.data.OptionT
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapter, FromAdapterF, ToAdapter}
import org.kote.client.notion._
import org.kote.client.notion.model.page.{NotionPageFullResponse, PageSearchRequest}
import org.kote.domain.task.Task
import org.kote.domain.task.Task.TaskId
import org.kote.repository.TaskRepository
import cats.implicits.toTraverseOps

class NotionTaskRepository[F[_]: Monad](
    pageClient: NotionPageClient[F],
    blockClient: NotionBlockClient[F],
)(implicit
    val taskAdapter: Adapter[Task, NotionPageCreateRequest, NotionPageFullResponse],
    val idAdapter: Adapter[TaskId, NotionPageId, NotionPageId],
) extends TaskRepository[F] {
  override def create(obj: Task): F[Long] =
    (for {
      pageResponse <- pageClient.create(obj.toRequest)
      blockResponse <- blockClient
        .getContent(obj.id.toRequest)
        .value
        .map(opt => opt.getOrElse(List()))
    } yield NotionPageFullResponse(pageResponse, blockResponse).fromResponse).as(1L)

  override def list: F[List[Task]] =
    pageClient
      .search(PageSearchRequest(None, None))
      .map(_.traverse { response =>
        blockClient
          .getContent(response.id)
          .getOrElse(List())
          .map(res => NotionPageFullResponse(response, res))
          .fromResponse
      })
      .flatten

  override def get(id: TaskId): OptionT[F, Task] =
    for {
      pageResponse <- pageClient.get(id.toRequest)
      blockResponse <- blockClient.getContent(id.toRequest)
    } yield NotionPageFullResponse(pageResponse, blockResponse).fromResponse

  override def delete(id: TaskId): OptionT[F, Task] =
    for {
      blockResponse <- blockClient.getContent(id.toRequest)
      response <- pageClient.achieve(id.toRequest)
    } yield NotionPageFullResponse(response, blockResponse).fromResponse

  // Тут что-то сложное
  override def update(
      id: Task.TaskId,
      cmds: List[TaskRepository.TaskUpdateCommand],
  ): OptionT[F, Task] = ???
}
