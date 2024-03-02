package org.kote.domain.group

import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.TaskId

import java.util.UUID

final case class Group(
    id: GroupId,
    title: String,
    tasks: List[TaskId],
) {
  def toResponse: GroupResponse =
    GroupResponse(id, title, tasks)
}

object Group {
  final case class GroupId private (inner: UUID) extends AnyVal

  def fromCreateGroup(uuid: UUID, createGroup: CreateGroup): Group =
    Group(GroupId(uuid), createGroup.title, createGroup.tasks)
}
