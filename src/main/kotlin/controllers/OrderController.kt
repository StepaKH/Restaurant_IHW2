package org.example.controllers


import org.example.data.controllers.order.addDishes.AddDishesToOrderRequestData
import org.example.data.controllers.order.addDishes.AddDishesToOrderResponseData
import org.example.data.controllers.order.cancellation.CancelOrderRequestData
import org.example.data.controllers.order.cancellation.CancelOrderResponseData
import org.example.data.controllers.order.pay.PayOrderRequestData
import org.example.data.controllers.order.pay.PayOrderResponseData
import org.example.data.controllers.order.place.PlaceOrderRequestData
import org.example.data.controllers.order.place.PlaceOrderResponseData
import org.example.data.controllers.order.rating.OrderRequestData
import org.example.data.controllers.order.rating.RateOrderResponseData
import org.example.data.controllers.order.removeDishes.RemoveDishFromOrderRequestData
import org.example.data.controllers.order.removeDishes.RemoveDishFromOrderResponseData
import org.example.data.controllers.order.showAll.GetOrdersResponseData
import org.example.exceptions.RestException
import org.example.services.OrderService
import org.example.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/order")
class OrderController(
    private val userService: UserService,
    private val orderService: OrderService,
) {
    private val logger: Logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping("/place - (Сделать заказ)")
    fun placeOrder(
        @RequestBody requestData: PlaceOrderRequestData,
    ): ResponseEntity<PlaceOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            val orderId = orderService.placeOrder(
                user, requestData.orderList
            )
            logger.info("Add ${requestData.orderList} order by ${user.login}")
            ResponseEntity.ok(PlaceOrderResponseData("Заказ добавлен", orderId))
        } catch (e: RestException) {
            logger.error("Error during add dish: $e")
            ResponseEntity.badRequest().body(PlaceOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during add dish: $e")
            ResponseEntity.badRequest().body(PlaceOrderResponseData("Неизвестная ошибка"))
        }
    }

    @PostMapping("/add-to-order - (Добавить блюдо к заказу)")
    fun addDishesToOrder(
        @RequestBody requestData: AddDishesToOrderRequestData,
    ): ResponseEntity<AddDishesToOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.addToOrder(
                user, requestData.orderId, requestData.orderList
            )
            logger.info("Add ${requestData.orderList} to order ${requestData.orderId} by ${user.login}")
            ResponseEntity.ok(AddDishesToOrderResponseData("Блюда были добавлены в заказ успешно"))
        } catch (e: RestException) {
            logger.error("Error during add dishes to order: $e")
            ResponseEntity.badRequest().body(AddDishesToOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during add dishes to order: $e")
            ResponseEntity.badRequest().body(AddDishesToOrderResponseData("Неизвестная ошибка"))
        }
    }

    @DeleteMapping("/remove-from-order - (Удалить блюдо из заказа)")
    fun removeDishesToOrder(
        @RequestBody requestData: RemoveDishFromOrderRequestData,
    ): ResponseEntity<RemoveDishFromOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.removeFromOrder(
                user, requestData.orderId, requestData.orderList
            )
            logger.info("Add ${requestData.orderList} to order ${requestData.orderId} by ${user.login}")
            ResponseEntity.ok(RemoveDishFromOrderResponseData("Блюда были удалены из заказа"))
        } catch (e: RestException) {
            logger.error("Error during remove dishes from order: $e")
            ResponseEntity.badRequest().body(RemoveDishFromOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during remove dishes from order: $e")
            ResponseEntity.badRequest().body(RemoveDishFromOrderResponseData("Неизвестная ошибка"))
        }
    }

    @PostMapping("/cancel - (Отменить заказ)")
    fun cancelOrder(
        @RequestBody requestData: CancelOrderRequestData,
    ): ResponseEntity<CancelOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.cancelOrder(
                user, requestData.orderId
            )
            logger.info("Cancel order ${requestData.orderId} by ${user.login}")
            ResponseEntity.ok(CancelOrderResponseData("Заказ был отменён"))
        } catch (e: RestException) {
            logger.error("Error during cancel order: $e")
            ResponseEntity.badRequest().body(CancelOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during cancel order: $e")
            ResponseEntity.badRequest().body(CancelOrderResponseData("Неизвестная ошибка"))
        }
    }

    @PostMapping("/pay - (Оплатить заказ)")
    fun payOrder(
        @RequestBody requestData: PayOrderRequestData,
    ): ResponseEntity<PayOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.payOrder(
                user, requestData.orderId
            )
            logger.info("Order ${requestData.orderId} payed by ${user.login}")
            ResponseEntity.ok(PayOrderResponseData("Заказ был оплачен"))
        } catch (e: RestException) {
            logger.error("Error during pay order: $e")
            ResponseEntity.badRequest().body(PayOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during pay order: $e")
            ResponseEntity.badRequest().body(PayOrderResponseData("Неизвестная ошибка"))
        }
    }

    @GetMapping("/all - (Показать все заказы)")
    fun getAllDishes(
        @RequestParam token: String?,
    ): ResponseEntity<GetOrdersResponseData> {
        return try {
            val user = userService.getUser(token)
            val orders = orderService.getOrders(user)
            logger.info("Get orders by ${user.login}")
            ResponseEntity.ok(GetOrdersResponseData("Информация обо всех заказах успешно получена", orders))
        } catch (e: RestException) {
            logger.error("Error during get orders: $e")
            ResponseEntity.badRequest().body(GetOrdersResponseData(e.message.toString(), null))
        } catch (e: Exception) {
            logger.error("Unknown error during get orders: $e")
            ResponseEntity.badRequest().body(GetOrdersResponseData("Неизвестная ошибка", null))
        }
    }

    @PostMapping("/rate - (Оценить заказ)")
    fun rateOrder(
        @RequestBody requestData: OrderRequestData,
    ): ResponseEntity<RateOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.rateOrder(
                user, requestData.orderId, requestData.mark, requestData.comment
            )
            logger.info("Order ${requestData.orderId} rated by ${user.login}")
            ResponseEntity.ok(RateOrderResponseData("Была получена оценка о заказе"))
        } catch (e: RestException) {
            logger.error("Error during rate order: $e")
            ResponseEntity.badRequest().body(RateOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during rate order: $e")
            ResponseEntity.badRequest().body(RateOrderResponseData("Неизвестная ошибка"))
        }
    }
}
