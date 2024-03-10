package org.example.data.controllers.order.showAll

import org.example.data.models.Order

class GetOrdersResponseData (
    val message: String,
    val orders: List<Order>?
)