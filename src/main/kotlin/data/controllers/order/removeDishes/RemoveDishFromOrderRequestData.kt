package org.example.data.controllers.order.removeDishes

import org.example.data.models.OrderItem

class RemoveDishFromOrderRequestData (
    val token: String?,
    val orderId: Int?,
    val orderList: List<OrderItem>?
)