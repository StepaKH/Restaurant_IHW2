package org.example.controllers

import org.example.data.controllers.restaurant.InfResponseData
import org.example.data.controllers.restaurant.StatisticResponseData
import org.example.data.models.Restaurant
import org.example.exceptions.RestException
import org.example.services.OrderProcessingService
import org.example.services.OrderService
import org.example.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/restaurant")
class RestaurantController(
    private val userService: UserService,
    private val orderService: OrderService,
    private val orderProcessingService: OrderProcessingService,
) {
    private val logger: Logger = LoggerFactory.getLogger(RestaurantController::class.java)

    @GetMapping("/info - (Получить данные о ресторане)")
    fun restaurantStatus(@RequestParam token: String?): ResponseEntity<InfResponseData> {
        try {
            val user = userService.getUser(token)
            orderService.updateTotalRevenue(user)
            orderProcessingService.updateWorkers(user)
            return ResponseEntity.ok(InfResponseData("Данные о ресторане", Restaurant.getInstance()))
        } catch (e: RestException) {
            logger.error("Error during get restaurant status: $e")
            return ResponseEntity.badRequest().body(InfResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during get restaurant status: $e")
            return ResponseEntity.badRequest().body(InfResponseData("Неизвестная ошибка"))
        }
    }

    @GetMapping("/statistic - (Получить статистику ресторана)")
    fun restaurantStatistic(@RequestParam token: String?): ResponseEntity<StatisticResponseData> {
        try {
            val user = userService.getUser(token)
            orderService.updateTotalRevenue(user)
            orderProcessingService.updateWorkers(user)
            val mostOrderedDishes = orderService.mostOrderedDishes()
            val ordersSortedByRating = orderService.ordersSortedByRating()
            val averageOrderPrice = orderService.averageOrderPrice()
            return ResponseEntity.ok(
                StatisticResponseData(
                    "Статистика ресторана",
                    Restaurant.getInstance(),
                    mostOrderedDishes,
                    ordersSortedByRating,
                    averageOrderPrice
                )
            )
        } catch (e: RestException) {
            logger.error("Error during get restaurant statistic: $e")
            return ResponseEntity.badRequest().body(StatisticResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during get restaurant statistic: $e")
            return ResponseEntity.badRequest().body(StatisticResponseData("Неизвестная ошибка"))
        }

    }
}
