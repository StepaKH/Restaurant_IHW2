package org.example.services

import kotlinx.coroutines.*
import org.example.data.files.WorkWithFiles
import org.example.data.models.Dish
import org.example.data.models.Order
import org.example.data.models.Restaurant
import org.example.data.models.User
import org.example.exceptions.RestException
import org.example.statuses.OrderStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.Semaphore

@Service
class OrderProcessingService(
    private val orderRepository: WorkWithFiles<Order>,
    private val dishRepository: WorkWithFiles<Dish>,
    @Value("\${restaurant.workers}") final val restaurantWorkers: Int,
) {
    private val logger: Logger = LoggerFactory.getLogger(OrderProcessingService::class.java)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val semaphore = Semaphore(restaurantWorkers)

    private data class OrderProcessingContext(val orderId: Int, val job: Job)

    private val orderQueue: Queue<Order> = LinkedList()

    private val orderProcessingContextMap = HashMap<Int, OrderProcessingContext>()

    fun processOrder(order: Order): Int {
        orderQueue.offer(order) // Добавляем заказ в конец очереди
        processNextOrder() // Начинаем обработку следующего заказа
        return order.id
    }

    private fun processNextOrder() {
        if (orderQueue.isNotEmpty()) {
            val order = orderQueue.poll() // Извлекаем первый заказ из очереди
            val orderId = order.id
            val job = coroutineScope.launch {
                try {
                    semaphore.acquire() // Запрашиваем разрешение
                    var allTimeSeconds: Long = 0
                    for (orderPosition in order.dishes) {
                        val dish = dishRepository.read(orderPosition.dishId)
                            ?: throw RestException("В меню нет позиции с id ${orderPosition.dishId}")
                        allTimeSeconds += orderPosition.quantity * dish.cookingTimeMinutes * 60
                    }
                    logger.info("Processing order: $order. It will take ${allTimeSeconds / 60} minutes")
                    order.status = OrderStatus.PREPARING
                    orderRepository.update(order)
                    delay(allTimeSeconds * 1000 / 60) // Emulate cooking
                    order.status = OrderStatus.READY
                    orderRepository.update(order)
                    val restaurant = Restaurant.getInstance()
                    restaurant.updateRevenue(order.price)
                    logger.info("Order processed: $order")
                } catch (e: CancellationException) {
                    logger.info("Order processing canceled: $order")
                } finally {
                    semaphore.release() // Освобождаем разрешение
                    processNextOrder() // Начинаем обработку следующего заказа
                }
            }
            val orderProcessingContext = OrderProcessingContext(orderId, job)
            orderProcessingContextMap[orderId] = orderProcessingContext
        }
    }

    fun cancel(orderId: Int) {
        val orderProcessingContext = orderProcessingContextMap[orderId]
            ?: throw RestException("Заказ с id $orderId не находится в процессе приготовления")
        val order = orderRepository.read(orderId)
            ?: throw RestException("Заказ с id $orderId не найден")
        if (order.status != OrderStatus.PREPARING) throw RestException("Заказ с id $orderId не находится в процессе приготовления")
        orderProcessingContext.job.cancel()
        order.status = OrderStatus.CANCELED
        orderRepository.update(order)
        orderProcessingContextMap.remove(orderId)
    }

    fun updateWorkers(user: User): Boolean {
        if (!user.isAdmin) throw RestException("Пользователь ${user.login} не является администратором")
        val restaurant = Restaurant.getInstance()
        restaurant.workers = restaurantWorkers
        return true
    }
}
