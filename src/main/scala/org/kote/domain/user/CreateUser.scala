package org.kote.domain.user

import org.kote.domain.user.User.UserPassword

final case class CreateUser(
    name: String,
    password: UserPassword,
)
