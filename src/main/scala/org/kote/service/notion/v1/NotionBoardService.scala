package org.kote.service.notion.v1

import cats.Monad
import cats.data.OptionT
import cats.effect.std.UUIDGen
import cats.implicits.{toFlatMapOps, toTraverseOps}
import cats.syntax.functor._
import org.kote.client.notion.model.database.{DbSearchRequest, DbUpdateRequest}
import org.kote.client.notion._
import org.kote.client.notion.model.page.{
  PageFilesPropertyResponse,
  PagePeoplePropertyResponse,
  PageRichTextPropertyResponse,
  PageSelectPropertyResponse,
  PageStatusPropertyResponse,
  PageTitlePropertyResponse,
}
import org.kote.client.notion.model.text.RichText
import org.kote.domain.board.Board.BoardId
import org.kote.domain.board.{Board, BoardResponse, CreateBoard}
import org.kote.domain.group.Group.GroupId
import org.kote.domain.user.User
import org.kote.domain.user.User.UserId
import org.kote.repository.{BoardRepository, GroupRepository, IntegrationRepository, TaskRepository}
import org.kote.service.BoardService

class NotionBoardService[F[_]: Monad: UUIDGen](
    // repositories
    boardRepository: BoardRepository[F],
    groupRepository: GroupRepository[F],
    taskRepository: TaskRepository[F],
    // clients
    notionDatabaseClient: NotionDatabaseClient[F],
    // integrations
    databaseIntegration: IntegrationRepository[F, BoardId, NotionDatabaseId],
    userMainPageIntegration: IntegrationRepository[F, UserId, NotionPageId],
    userToUserIntegration: IntegrationRepository[F, UserId, NotionUserId],
    propertyIntegration: IntegrationRepository[F, GroupId, PropertyId],
) extends BoardService[F] {

  override def create(createBoard: CreateBoard): F[BoardResponse] =
    (for {
      uuid <- UUIDGen[F].randomUUID
      board = Board.fromCreateBoard(uuid, createBoard)
      _ <- boardRepository.create(board)
      result <- (for {
        mainPage <- userMainPageIntegration.getByKey(createBoard.createdBy)
        response <- notionDatabaseClient.create(CreateBoard.toNotionRequest(createBoard, mainPage))
        _ <- OptionT.liftF(databaseIntegration.set(board.id, response.id))
      } yield response).value
    } yield BoardResponse
      .fromNotionResponse(result, List.empty)(
        databaseIntegration,
        userToUserIntegration,
        propertyIntegration,
      )
      .getOrElse(board.toResponse)).flatten

  override def list(user: User.UserId): F[List[BoardResponse]] =
    (for {
      notionUserId <- userToUserIntegration.getByKey(user)
      databases <- notionDatabaseClient.search(DbSearchRequest(None, None))
      filtered <- OptionT.pure[F](
        databases.filter(response => response.createdBy.id == notionUserId),
      )
      properties <- filtered.traverse(getProperties)
      zipped = filtered.zip(properties)
      mapped <- zipped.traverse(zip =>
        BoardResponse.fromNotionResponse(Some(zip._1), zip._2)(
          databaseIntegration,
          userToUserIntegration,
          propertyIntegration,
        ),
      )
    } yield mapped).getOrElse(List.empty)

  override def get(id: Board.BoardId): OptionT[F, BoardResponse] =
    for {
      databaseId <- databaseIntegration.getByKey(id)
      database <- notionDatabaseClient.get(databaseId)
      properties <- getProperties(database)
      res <- BoardResponse.fromNotionResponse(Some(database), properties)(
        databaseIntegration,
        userToUserIntegration,
        propertyIntegration,
      )
    } yield res

  override def update(
      id: Board.BoardId,
      cmds: List[BoardRepository.BoardUpdateCommand],
  ): OptionT[F, BoardResponse] =
    for {
      board <- boardRepository.update(id, cmds)
      databaseId <- databaseIntegration.getByKey(id)
      database <- notionDatabaseClient.update(
        databaseId,
        DbUpdateRequest(Some(List(RichText.text(board.title))), Map.empty),
      )
      properties <- getProperties(database)
      res <- BoardResponse.fromNotionResponse(Some(database), properties)(
        databaseIntegration,
        userToUserIntegration,
        propertyIntegration,
      )
    } yield res

  override def delete(id: Board.BoardId): OptionT[F, BoardResponse] =
    for {
      deleted <- boardRepository.delete(id)
      groups <- deleted.groups.traverse(groupRepository.delete)
      _ <- groups.traverse(_.tasks.traverse(taskRepository.delete))
      _ <- databaseIntegration.delete(deleted.id)
    } yield deleted.toResponse

  private def getProperties(
      database: NotionDatabaseResponse,
  ): OptionT[F, List[PropertyId]] =
    for {
      databasesPages <- notionDatabaseClient.list(database.id)
      properties = databasesPages
        .map { page =>
          val key = PropertiesNames.groupPropertyName
          for {
            name <- page.properties.get(key).map(_.id)
            value <- page.properties
              .get(key)
              .flatMap(_.value match {
                case PageFilesPropertyResponse(_)       => None
                case PagePeoplePropertyResponse(_)      => None
                case PageRichTextPropertyResponse(text) => Some(text.mkString)
                case PageStatusPropertyResponse(_, _)   => None
                case PageTitlePropertyResponse(_)       => None
                case PageSelectPropertyResponse(_, _)   => None
              })
          } yield PropertyId(key, name, value)
        }
        .filterNot(_ == Option.empty)
        .flatMap {
          case None        => List.empty
          case Some(value) => List(value)
        }
        .distinct
    } yield properties
}
