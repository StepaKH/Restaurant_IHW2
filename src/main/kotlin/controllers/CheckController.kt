package org.example.controllers

import org.example.data.controllers.check.FillFalseResponseDetails
import org.example.data.models.User
import org.example.exceptions.RestException
import org.example.services.DishService
import org.example.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CheckController(
    private val userService: UserService,
    private val dishService: DishService,
) {
    private val logger: Logger = LoggerFactory.getLogger(CheckController::class.java)

    @PostMapping("/fill-mock-data - (Добавить тестовые данные)")
    fun fillMockData(): ResponseEntity<FillFalseResponseDetails> {
        return try {
            val token1 = userService.register("IAdmin@ya.ru", "admin", true)
            val user1 = userService.getUser(token1)
            generateRandomDishes(user1, 10)
            val token2 = userService.register("ivan@mail.ru", "pass123", false)
            val user2 = userService.getUser(token2)
            val token3 = userService.register("sonya@gmail.ru", "sonya2004", false)
            val user3 = userService.getUser(token3)
            logger.info("Mock data has been filled successfully")
            ResponseEntity.ok(
                FillFalseResponseDetails(
                    "Тестовые данные были успешно добавлены",
                    listOf(user1, user2, user3)
                )
            )
        } catch (e: RestException) {
            logger.error("Error during filling mock data: $e")
            ResponseEntity.badRequest().body(FillFalseResponseDetails(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during filling mock data: $e")
            ResponseEntity.badRequest().body(FillFalseResponseDetails("Произошла неизвестная ошибка"))
        }
    }


    private fun generateRandomDishes(user: User, count: Int) {
        val dishTitles = listOf("Meat", "Pasta", "Juice", "Salad", "Potato", "Tomato", "Muffin")
        val dishDescriptions = listOf(
            "delicious", "yummy", "tasty", "appetizing", "flavorful", "satisfying",
            "scrumptious", "mouthwatering", "savory", "delectable"
        )
        for (i in 1..count) {
            val name = dishTitles.random()
            val description = dishDescriptions.random()
            val quantity = (1..100).random()
            val cookingTime = (1..5).random()
            val price = (10..1000).random().toDouble()
            dishService.addDish(user, name, description, quantity, cookingTime, price)
        }
    }
}
