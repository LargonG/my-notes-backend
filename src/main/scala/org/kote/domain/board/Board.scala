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
) {
  def toResponse: BoardResponse =
    BoardResponse(id, title, owner, groups)
}

object Board {
  final case class BoardId private (inner: UUID) extends AnyVal

  def fromCreateBoard(uuid: UUID, createBoard: CreateBoard): Board =
    Board(BoardId(uuid), createBoard.title, createBoard.owner, List())
}
