package org.kote.domain.group

import org.kote.domain.group.Group.GroupId
import org.kote.domain.task.Task.TaskId

case class GroupResponse(
    id: GroupId,
    title: String,
    tasks: List[TaskId],
)
