package org.example.data.controllers.check

import org.example.data.models.User

class FillFalseResponseDetails (
    val message: String,
    val users: List<User>? = null
)