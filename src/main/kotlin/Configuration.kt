package org.example

import org.example.data.files.WorkWithFiles
import org.example.data.models.Dish
import org.example.data.models.Order
import org.example.data.models.User
import org.example.repositories.DishRepository
import org.example.repositories.OrderRepository
import org.example.repositories.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration {
    @Bean
    fun userRepository(): WorkWithFiles<User> {
        return UserRepository()
    }

    @Bean
    fun dishRepository(): WorkWithFiles<Dish> {
        return DishRepository()
    }

    @Bean
    fun orderRepository(): WorkWithFiles<Order> {
        return OrderRepository()
    }

}