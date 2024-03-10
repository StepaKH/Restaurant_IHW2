package org.example.controllers

import org.example.data.controllers.dish.add.AddDishRequestData
import org.example.data.controllers.dish.add.AddDishResponseData
import org.example.data.controllers.dish.delete.DeleteDishRequestData
import org.example.data.controllers.dish.delete.DeleteDishResponseData
import org.example.data.controllers.dish.showAll.GetDishesResponseData
import org.example.exceptions.RestException
import org.example.services.DishService
import org.example.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/dish")
class DishController(private val userService: UserService, private val dishService: DishService) {
    private val logger: Logger = LoggerFactory.getLogger(DishController::class.java)

    @PostMapping("/add - (Добавить блюдо в меню)")
    fun addDish(
        @RequestBody requestData: AddDishRequestData,
    ): ResponseEntity<AddDishResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            dishService.addDish(
                user,
                requestData.dishName,
                requestData.dishDescription,
                requestData.dishQuantity,
                requestData.dishCookingTimeMinutes,
                requestData.dishPrice
            )
            logger.info("Add ${requestData.dishName} dish by ${user.login}")
            ResponseEntity.ok(AddDishResponseData("Блюдо добавлено"))
        } catch (e: RestException) {
            logger.error("Error during add dish: $e")
            ResponseEntity.badRequest().body(AddDishResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during add dish: $e")
            ResponseEntity.badRequest().body(AddDishResponseData("Неизвестная ошибка"))
        }
    }

    @DeleteMapping("/delete - (Удалить блюдо из меню)")
    fun deleteDish(
        @RequestBody requestData: DeleteDishRequestData,
    ): ResponseEntity<DeleteDishResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            dishService.deleteDish(user, requestData.dishId)
            logger.info("Delete dish id ${requestData.dishId} by ${user.login}")
            ResponseEntity.ok(DeleteDishResponseData("Блюдо удалено"))
        } catch (e: RestException) {
            logger.error("Error during delete dish: $e")
            ResponseEntity.badRequest().body(DeleteDishResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during delete dish: $e")
            ResponseEntity.badRequest().body(DeleteDishResponseData("Неизвестная ошибка"))
        }
    }

    @GetMapping("/all - (Показать все блюда)")
    fun getAllDishes(
        @RequestParam token: String?,
    ): ResponseEntity<GetDishesResponseData> {
        return try {
            val user = userService.getUser(token)
            val dishes = dishService.getDishes(user)
            logger.info("Get dishes by ${user.login}")
            ResponseEntity.ok(GetDishesResponseData("Информация о всех блюдах успешно получена", dishes))
        } catch (e: RestException) {
            logger.error("Error during get dishes: $e")
            ResponseEntity.badRequest().body(GetDishesResponseData(e.message.toString(), null))
        } catch (e: Exception) {
            logger.error("Unknown error during get dishes: $e")
            ResponseEntity.badRequest().body(GetDishesResponseData("Неизвестная ошибка", null))
        }
    }
}
