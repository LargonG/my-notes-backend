package org.kote.domain.board

import org.kote.domain.group.Group.GroupId
import org.kote.domain.board.Board.BoardId
import org.kote.domain.user.User.UserId

import java.util.UUID

final case class Board(
    id: BoardId,
    title: String,
    owner: UserId,
    groups: List[GroupId],
)

object Board {
  final case class BoardId(inner: UUID) extends AnyVal
}
