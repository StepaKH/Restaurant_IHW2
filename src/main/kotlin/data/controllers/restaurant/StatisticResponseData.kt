package org.example.data.controllers.restaurant

import org.example.data.models.Order
import org.example.data.models.Restaurant

data class StatisticResponseData (
    val message: String,
    val restaurant: Restaurant? = null,
    val mostOrderedDishes: Map<Int, Int>? = null,
    val ordersSortedByRating: List<Order>? = null,
    val averageOrderPrice: Double? = null,
)