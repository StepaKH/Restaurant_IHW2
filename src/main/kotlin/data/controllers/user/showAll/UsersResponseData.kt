package org.example.data.controllers.user.showall

import org.example.data.models.User

class UsersResponseData (
    val message: String,
    val users: List<User>?
)