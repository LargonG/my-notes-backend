package org.kote.domain.comment

import org.kote.domain.content.Content
import org.kote.domain.user.User.UserId

final case class CreateComment(
    author: UserId,
    content: Content,
)
