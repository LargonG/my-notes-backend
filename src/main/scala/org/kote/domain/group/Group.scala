package org.kote.domain.group

import org.kote.common.tethys.TethysInstances
import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.TaskId
import sttp.tapir.Schema
import tethys.{JsonReader, JsonWriter}

import java.util.UUID

final case class Group(
    id: GroupId,
    parent: BoardId,
    title: String,
    tasks: List[TaskId],
) {
  def toResponse: GroupResponse =
    GroupResponse(id, title, tasks)
}

object Group {
  final case class GroupId(inner: UUID) extends AnyVal

  object GroupId extends TethysInstances {
    implicit val groupIdReader: JsonReader[GroupId] = JsonReader[UUID].map(GroupId.apply)
    implicit val groupIdWriter: JsonWriter[GroupId] = JsonWriter[UUID].contramap(_.inner)
    implicit val groupIdSchema: Schema[GroupId] = Schema.derived.description("ID колонки")
  }

  def fromCreateGroup(uuid: UUID, createGroup: CreateGroup): Group =
    Group(GroupId(uuid), createGroup.parent, createGroup.title, createGroup.tasks)
}
