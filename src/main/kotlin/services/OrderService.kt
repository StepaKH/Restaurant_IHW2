package org.example.services

import org.example.data.files.WorkWithFiles
import org.example.data.models.*
import org.example.exceptions.RestException
import org.example.statuses.OrderStatus
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: WorkWithFiles<Order>,
    private val dishRepository: WorkWithFiles<Dish>,
    private val orderProcessingService: OrderProcessingService,
) {

    fun placeOrder(user: User, orderList: List<OrderItem>?): Int {
        if (orderList == null) throw RestException("Заказ не может быть пустым")
        val newOrder = Order(user.id, OrderStatus.ACCEPTED)
        for (orderPosition in orderList) {
            val dish = dishRepository.read(orderPosition.dishId)
                ?: throw RestException("В меню нет позиции с id ${orderPosition.dishId}")
            if (orderPosition.quantity > dish.quantity) throw RestException("Вы не можете заказать ${orderPosition.quantity} порций, так как в меню их всего ${dish.quantity}")
            dish.quantity -= orderPosition.quantity
            dishRepository.update(dish)
            newOrder.addDish(orderPosition)
            newOrder.price += dish.price * orderPosition.quantity
        }
        val isCreated = orderRepository.create(newOrder)
        if (!isCreated) throw RestException("Ошибка подключения к базе данных")
        val id = orderProcessingService.processOrder(newOrder)
        return id
    }

    fun addToOrder(user: User, orderId: Int?, orderList: List<OrderItem>?): Boolean {
        if (orderId == null) throw RestException("Не указан id заказа")
        if (orderList == null) throw RestException("Заказ не может быть пустым")
        val order = orderRepository.read(orderId) ?: throw RestException("Заказ с id $orderId не найден")
        if (order.status == OrderStatus.PREPARING) throw RestException("Заказ находится в процессе приготовления")
        if (order.status == OrderStatus.READY) throw RestException("Заказ уже готов")
        if (order.userId != user.id && !user.isAdmin) throw RestException("Заказ с id $orderId не принадлежит пользователю ${user.login} и пользователь не является администратором")
        for (orderPosition in orderList) {
            val dish = dishRepository.read(orderPosition.dishId)
                ?: throw RestException("В меню нет позиции с id ${orderPosition.dishId}")
            dish.quantity -= orderPosition.quantity
            dishRepository.update(dish)
            order.addDish(orderPosition)
            order.price += dish.price * orderPosition.quantity
        }
        val isUpdated = orderRepository.update(order)
        if (!isUpdated) throw RestException("Ошибка подключения к базе данных")
        return true
    }

    fun removeFromOrder(user: User, orderId: Int?, orderList: List<OrderItem>?): Boolean {
        if (orderId == null) throw RestException("Не указан id заказа")
        if (orderList == null) throw RestException("Заказ не может быть пустым")
        val order = orderRepository.read(orderId) ?: throw RestException("Заказ с id $orderId не найден")
        if (order.userId != user.id && !user.isAdmin) throw RestException("Заказ с id $orderId не принадлежит пользователю ${user.login} и пользователь не является администратором")
        if (order.status == OrderStatus.PREPARING) throw RestException("Заказ находится в процессе приготовления")
        if (order.status == OrderStatus.READY) throw RestException("Заказ уже готов")
        for (orderPosition in orderList) {
            val actualOrderPosition = order.dishes.firstOrNull { it.dishId == orderPosition.dishId }
            if (actualOrderPosition == null) throw RestException("В заказе нет позиции с id ${orderPosition.dishId}")
            val dish = dishRepository.read(orderPosition.dishId)
                ?: throw RestException("В меню нет позиции с id ${orderPosition.dishId}")
            dish.quantity += orderPosition.quantity
            dishRepository.update(dish)
            orderRepository.update(order)
            order.removeDish(orderPosition)
            order.price -= dish.price * orderPosition.quantity
        }
        val isUpdated = orderRepository.update(order)
        if (!isUpdated) throw RestException("Ошибка подключения к базе данных")
        return true
    }

    fun cancelOrder(user: User, orderId: Int?): Boolean {
        if (orderId == null) throw RestException("Не указан id заказа")
        val order = orderRepository.read(orderId) ?: throw RestException("Заказ с id $orderId не найден")
        if (order.userId != user.id && !user.isAdmin) throw RestException("Заказ с id $orderId не принадлежит пользователю ${user.login} и пользователь не является администратором")
        if (order.status == OrderStatus.READY) throw RestException("Заказ уже готов")
        orderProcessingService.cancel(orderId)
        return true
    }

    fun payOrder(user: User, orderId: Int?): Boolean {
        if (orderId == null) throw RestException("Не указан id заказа")
        val order = orderRepository.read(orderId) ?: throw RestException("Заказ с id $orderId не найден")
        if (order.userId != user.id && !user.isAdmin) throw RestException("Заказ с id $orderId не принадлежит пользователю ${user.login} и пользователь не является администратором")
        if (order.status == OrderStatus.PAID) throw RestException("Заказ уже оплачен")
        if (order.status == OrderStatus.CANCELED) throw RestException("Заказ был отменён")
        if (order.status != OrderStatus.READY) throw RestException("Заказ ещё не готов")
        order.status = OrderStatus.PAID
        val isUpdated = orderRepository.update(order)
        if (!isUpdated) throw RestException("Ошибка подключения к базе данных")
        return true
    }

    fun rateOrder(user: User, orderId: Int?, mark: Int?, comment: String?): Boolean {
        if (orderId == null) throw RestException("Не указан id заказа")
        if (mark == null) throw RestException("Не поставлена оценка")
        if (comment == null) throw RestException("Не оставлен комментарий")
        val order = orderRepository.read(orderId) ?: throw RestException("Заказ с id $orderId не найден")
        if (order.userId != user.id && !user.isAdmin) throw RestException("Заказ с id $orderId не принадлежит пользователю ${user.login} и пользователь не является администратором")
        if (order.status != OrderStatus.PAID) throw RestException("Заказ не оплачен")
        val review = Review(mark, comment)
        order.review = review
        val isUpdated = orderRepository.update(order)
        if (!isUpdated) throw RestException("Ошибка подключения к базе данных")
        return true
    }

    fun getOrders(user: User): List<Order> {
        return if (user.isAdmin) {
            orderRepository.readAll()
        } else {
            orderRepository.readAll().filter { it.userId == user.id }
        }
    }

    fun updateTotalRevenue(user: User): Boolean {
        if (!user.isAdmin) throw RestException("Пользователь ${user.login} не является администратором")
        val totalRevenue = orderRepository.readAll().filter { it.status == OrderStatus.PAID }.sumOf { it.price }
        val restaurant = Restaurant.getInstance()
        restaurant.totalRevenue = totalRevenue
        return true
    }

    fun mostOrderedDishes(): Map<Int, Int> {
        val allOrders = orderRepository.readAll()
        val dishCountMap = mutableMapOf<Int, Int>()
        allOrders.forEach { order ->
            order.dishes.forEach { orderPosition ->
                val dishId = orderPosition.dishId
                dishCountMap[dishId] = dishCountMap.getOrDefault(dishId, 0) + orderPosition.quantity
            }
        }
        return dishCountMap.toList().sortedByDescending { (_, quantity) -> quantity }.toMap()
    }


    fun ordersSortedByRating(): List<Order> {
        return orderRepository.readAll().filter { it.review != null }.sortedByDescending { it.review?.mark }
    }

    fun averageOrderPrice(): Double {
        val allOrders = orderRepository.readAll()
        val totalAmount = allOrders.sumOf { it.price }
        return if (allOrders.isNotEmpty()) totalAmount / allOrders.size else 0.0
    }


}
