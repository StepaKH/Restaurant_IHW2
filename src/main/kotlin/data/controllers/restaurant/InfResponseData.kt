package org.example.data.controllers.restaurant

import org.example.data.models.Restaurant

data class InfResponseData (
    val message: String,
    val restaurant: Restaurant? = null
)