package org.kote.repository.notion

import cats.Monad
import cats.data.OptionT
import cats.implicits.{catsSyntaxApplicativeId, toTraverseOps}
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.kote.adapter.Adapter
import org.kote.adapter.Adapter.{FromAdapter, FromAdapterF, ToAdapter}
import org.kote.client.notion._
import org.kote.client.notion.model.page.{NotionPageFullResponse, PageSearchRequest}
import org.kote.domain.board.Board
import org.kote.domain.group.Group
import org.kote.domain.task.Task
import org.kote.domain.task.Task.TaskId
import org.kote.repository.TaskRepository

class NotionTaskRepository[F[_]: Monad](
    pageClient: NotionPageClient[F],
    blockClient: NotionBlockClient[F],
)(implicit
    val taskAdapter: Adapter[Task, NotionPageCreateRequest, NotionPageFullResponse],
    val idAdapter: Adapter[TaskId, NotionPageId, NotionPageId],
) extends TaskRepository[F] {

  /** Создаёт задачу, используя notion api page client, на стороне Notion будет создана страница в
    * базе данных, которая является связанной с внутренней таблицей, в которой лежит наша задача.
    * @param task
    *   внутреннее представление задачи.
    * @return
    *   1, если получилось добавить. Иначе - todo ошибка кидается в F, по контексту не очевидно
    */
  override def create(task: Task): F[Long] =
    (for {
      response <- pageClient.create(task.toRequest)
    } yield response).as(1L).getOrElse(0L)

  /** Возвращает список из всех задач, что нам доступны в notion. Использует notion api page client,
    * чтобы получить список всех страниц, как известно, страницы в notion могут лежать и не в базах
    * данных, поэтому их нужно todo отфильтровать, также фильтровать архивированные
    * @return
    *   все задачки
    */
  /*
    todo: None или не None, вот в чём вопрос
   */
  override def all: F[List[Task]] =
    pageClient
      .search(PageSearchRequest(None, None))
      .map(_.traverse { response =>
        blockClient
          .getContent(response.id)
          .getOrElse(List())
          .map(res => NotionPageFullResponse(response, res))
          .fromResponse
      })
      .getOrElse(List[Task]().pure)
      .flatten

  override def listByGroup(groupId: Group.GroupId): OptionT[F, List[Task]] = ???

  override def listByBoard(boardId: Board.BoardId): OptionT[F, List[Task]] = ???

  /** Получает задачу по её id. Использует Notion api page client, чтобы получить общую информацию о
    * странице. Использует notion api block client, чтобы получить информацию о её детях - контенте,
    * контент получает поверхностно, а это значит, что warning: наша программа не поддерживает
    * вложенные структуры контента.
    * @param id
    *   задачки, внутренняя структура
    * @return
    *   None, если нет связки задачка - страница, или если сервер вернул ошибку.
    */
  /*
    todo: block client кидает None, если проблемы с сервером, а мы это возвращаем как "нет элемента".
    todo: нет поддержки вложенных блоков (? нужно ли это вообще ?)
   */
  override def get(id: TaskId): OptionT[F, Task] =
    for {
      pageResponse <- pageClient.get(id.toRequest)
      blockResponse <- blockClient.getContent(id.toRequest)
    } yield NotionPageFullResponse(pageResponse, blockResponse).fromResponse

  /** Удаляет задачку по её id. Использует Notion api block client, чтобы получить контент, перед
    * тем, как архивировать страницу. После использует notion api page client, чтобы архивировать
    * связанную с задачкой страницу notion. Таким образом мы всё ещё сможем получить доступ к этой
    * странице, но для пользователя будет казаться, что её не существует.
    * @param id
    *   задачки, внутренняя структура
    * @return
    *   None - если нет связки задачка -> страница, если сервер вернул ошибку. Если всё получилось -
    *   возвращает Task внутри Option и F.
    */
  /*
    todo: клиенты кидают None, если проблемы с сервером (не получилось достать), а у нас это смысл "нет элемента"
    todo: понять, что происходит с задачкой, когда её архивируют. Когда мы можем её получить.
   */
  override def delete(id: TaskId): OptionT[F, Task] =
    for {
      blockResponse <- blockClient.getContent(id.toRequest)
      response <- pageClient.achieve(id.toRequest)
    } yield NotionPageFullResponse(response, blockResponse).fromResponse

  /*
    todo: написать функцию, которая будет обновлять:
      название (title)
      контент (через blockClient) (удаление старых блоков -- создание новых)
      статус
      исполнителей (people (которое привязано к нашей задаче (узнаём с помощью специальной таблицы)))
   */
  override def update(
      id: Task.TaskId,
      cmds: List[TaskRepository.TaskUpdateCommand],
  ): OptionT[F, Task] = ???
}
