package org.example.services
import org.example.data.files.WorkWithFiles
import org.example.data.models.Dish
import org.example.data.models.User
import org.example.exceptions.RestException
import org.example.statuses.OrderStatus
import org.springframework.stereotype.Service

@Service
class DishService(private val dishRepository: WorkWithFiles<Dish>, private val orderService: OrderService) {
    fun addDish(
        user: User,
        name: String?,
        description: String?,
        quantity: Int?,
        dishCookingTimeMinutes: Int?,
        price: Double?,
    ): Boolean {
        if (!user.isAdmin) throw RestException("Пользователь не является администратором")
        if (name == null || description == null || quantity == null || dishCookingTimeMinutes == null || price == null) {
            throw RestException("Переданы не все параметры")
        }
        if (dishCookingTimeMinutes <= 0) throw RestException("Время приготовления в минутах должно быть положительным")
        if (price < 0) throw RestException("Цена должна быть неотрицательной")
        if (quantity <= 0) throw RestException("Количество должно быть положительным")
        val newDish = Dish(name, description, quantity, dishCookingTimeMinutes, price)
        val isCreate = dishRepository.create(newDish)
        if (!isCreate) throw RestException("Ошибка подключения к базе данных")
        return isCreate
    }

    fun deleteDish(user: User, dishId: Int?): Boolean {
        if (!user.isAdmin) throw RestException("Пользователь не является администратором")
        if (dishId == null) throw RestException("Переданы не все параметры")
        val orders = orderService.getOrders(user)
        for (order in orders) {
            if (order.status == OrderStatus.READY) continue
            for (dish in order.dishes) {
                if (dish.dishId == dishId) throw RestException("Вы не можете удалить блюдо, так как оно находится в процессе приготовления")
            }
        }
        val isDelete = dishRepository.delete(dishId)
        if (!isDelete) throw RestException("Неверный id")
        return true
    }

    fun getDishes(user: User): List<Dish> {
        return dishRepository.readAll()
    }
}
