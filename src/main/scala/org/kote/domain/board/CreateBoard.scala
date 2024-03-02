package org.kote.domain.board

import org.kote.domain.user.User.UserId

final case class CreateBoard(
    title: String,
    owner: UserId,
)
