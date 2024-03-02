package org.kote.domain.group

import org.kote.domain.task.Task.TaskId

case class CreateGroup(
    title: String,
    tasks: List[TaskId],
)
