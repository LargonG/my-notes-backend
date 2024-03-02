package org.kote.domain.board

import org.kote.domain.board.Board.BoardId
import org.kote.domain.group.Group.GroupId
import org.kote.domain.user.User.UserId

case class BoardResponse(
    id: BoardId,
    title: String,
    owner: UserId,
    groups: List[GroupId],
)
