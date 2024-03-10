package org.example.data.controllers.order.place

import org.example.data.models.OrderItem

class PlaceOrderRequestData (
    val token: String?,
    val orderList: List<OrderItem>?
)