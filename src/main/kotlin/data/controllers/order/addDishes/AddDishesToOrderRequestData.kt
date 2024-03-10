package org.example.data.controllers.order.addDishes

import org.example.data.models.OrderItem

class AddDishesToOrderRequestData (
    val token: String?,
    val orderId: Int?,
    val orderList: List<OrderItem>?
)