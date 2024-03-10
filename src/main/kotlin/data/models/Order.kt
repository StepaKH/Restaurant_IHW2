package org.example.data.models

import org.example.data.files.SerializableInterface
import org.example.exceptions.RestException
import org.example.statuses.OrderStatus
import java.util.*

class Order : SerializableInterface{
    constructor(userId: Int, status: OrderStatus, startTime: Date = Date(System.currentTimeMillis())) {
        this.userId = userId
        this.startTime = startTime
        this.status = status
        this.dishes = mutableListOf()
    }

    private var id1: Int = 0
    private var userId1: Int = 0
    private lateinit var startTime1: Date
    private lateinit var status1: OrderStatus
    private lateinit var dishes1: MutableList<OrderItem>
    private var price1: Double = 0.0
    private var review1: Review? = null

    var userId: Int
        get() {
            return userId1
        }
        set(value) {
            userId1 = value
        }
    var startTime: Date
        get() {
            return startTime1
        }
        set(value) {
            startTime1 = value
        }
    var status: OrderStatus
        get() {
            return status1
        }
        set(value) {
            status1 = value
        }
    var dishes: List<OrderItem>
        get() {
            return dishes1
        }
        private set(value) {
            dishes1 = value.toMutableList()
        }
    var price: Double
        get() {
            return price1
        }
        set(value) {
            price1 = value
        }
    var review: Review?
        get() {
            return review1
        }
        set(value) {
            review1 = value
        }

    override var id: Int
        get() {
            return id1
        }
        set(value) {
            id1 = value
        }

    override fun toString(): String {
        return "Order(id=$id1, userId=$userId1, startTime=$startTime1, status=$status1, dishes=$dishes1, price=$price1, review=$review1)"
    }

    fun addDish(orderItem: OrderItem) {
        val existingDish = dishes1.find { it.dishId == orderItem.dishId }
        if (existingDish != null) {
            val newQuantity = existingDish.quantity + orderItem.quantity
            if (newQuantity <= 0) throw RestException("Количество блюд должно быть положительным")
            dishes1.remove(existingDish)
            dishes1.add(OrderItem(orderItem.dishId, newQuantity))
        } else {
            if (orderItem.quantity <= 0) throw RestException("Количество блюд должно быть положительным")
            dishes1.add(OrderItem(orderItem.dishId, orderItem.quantity))
        }
    }

    fun removeDish(orderItem: OrderItem) {
        val existingDish = dishes1.find { it.dishId == orderItem.dishId }
        if (existingDish != null) {
            if (existingDish.quantity - orderItem.quantity < 0) throw RestException("Из заказа нельзя удалить ${orderItem.quantity} блюд, так как там их всего ${existingDish.quantity}")
            val newQuantity = existingDish.quantity - orderItem.quantity
            if (newQuantity > 0) {
                dishes1.remove(existingDish)
                dishes1.add(OrderItem(orderItem.dishId, newQuantity))
            } else {
                dishes1.remove(existingDish)
            }
        }
    }

}