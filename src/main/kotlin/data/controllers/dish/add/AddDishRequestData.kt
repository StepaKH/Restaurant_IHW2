package org.example.data.controllers.dish.add

class AddDishRequestData (
    val token: String?,
    val dishName: String?,
    val dishDescription: String?,
    val dishQuantity: Int?,
    val dishCookingTimeMinutes: Int?,
    val dishPrice: Double?,
)