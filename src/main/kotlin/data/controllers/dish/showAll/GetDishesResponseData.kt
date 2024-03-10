package org.example.data.controllers.dish.showAll

import org.example.data.models.Dish

class GetDishesResponseData (
    val message: String,
    val dishes: List<Dish>?
)