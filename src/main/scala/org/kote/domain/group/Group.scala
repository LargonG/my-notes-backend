package org.kote.domain.group

import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.TaskId

import java.util.UUID

final case class Group(
    id: GroupId,
    title: String,
    tasks: List[TaskId],
)

object Group {
  final case class GroupId(inner: UUID) extends AnyVal
}
