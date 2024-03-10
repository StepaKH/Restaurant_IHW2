package org.example.controllers

import org.example.data.controllers.user.auth.LoginRequestData
import org.example.data.controllers.user.auth.LoginResponseData
import org.example.data.controllers.user.logout.LogoutRequestData
import org.example.data.controllers.user.logout.LogoutResponseData
import org.example.data.controllers.user.register.RegRequestData
import org.example.data.controllers.user.register.RegResponseData
import org.example.data.controllers.user.showall.UsersResponseData
import org.example.exceptions.RestException
import org.example.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class UserController(private val userService: UserService) {
    private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/register - (Зарегистрироваться)")
    fun register(
        @RequestBody registerRequestData: RegRequestData,
    ): ResponseEntity<RegResponseData> {
        return try {
            val token = userService.register(
                registerRequestData.login, registerRequestData.password, registerRequestData.isAdmin
            )
            logger.info("User ${registerRequestData.login} registered")
            ResponseEntity.ok(RegResponseData("Пользователь зарегистрирован", token))
        } catch (e: RestException) {
            logger.error("Error during register: $e")
            ResponseEntity.badRequest().body(RegResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during register: $e")
            ResponseEntity.badRequest().body(RegResponseData("Неизвестная ошибка"))
        }
    }

    @PostMapping("/auth - (Войти в систему)")
    fun auth(
        @RequestBody authRequestData: LoginRequestData,
    ): ResponseEntity<LoginResponseData> {
        return try {
            val user = userService.getUser(authRequestData.login, authRequestData.password)
            val token = userService.authenticate(user)
            logger.info("User ${authRequestData.login} authenticated")
            ResponseEntity.ok(LoginResponseData("Пользователь вошёл в систему", token))
        } catch (e: RestException) {
            logger.error("Error during authenticate: $e")
            ResponseEntity.badRequest().body(LoginResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during authenticate: $e")
            ResponseEntity.badRequest().body(LoginResponseData("Неизвестная ошибка"))
        }
    }

    @PostMapping("/logout - (Выйти из системы)")
    fun logout(
        @RequestBody logoutRequestData: LogoutRequestData,
    ): ResponseEntity<LogoutResponseData> {
        return try {
            val user = userService.getUser(logoutRequestData.token)
            userService.logout(user)
            logger.info("User ${user.login} logged out")
            ResponseEntity.ok(LogoutResponseData("Пользователь вышел из системы"))
        } catch (e: RestException) {
            logger.error("Error during logout: $e")
            ResponseEntity.badRequest().body(LogoutResponseData(e.message.toString()))
        } catch (e: Exception) {
            logger.error("Unknown error during logout: $e")
            ResponseEntity.badRequest().body(LogoutResponseData("Неизвестная ошибка"))
        }
    }


    @GetMapping("/users - (Получить всех пользователей)")
    fun users(@RequestParam token: String?): ResponseEntity<UsersResponseData> {
        println(token)
        return try {
            val user = userService.getUser(token)
            val users = userService.getUsers(user)
            logger.info("Get users by ${user.login}")
            ResponseEntity.ok(UsersResponseData("Информация обо всех пользователях успешна получена", users))
        } catch (e: RestException) {
            logger.error("Error during get users: $e")
            ResponseEntity.badRequest().body(UsersResponseData(e.message.toString(), null))
        } catch (e: Exception) {
            logger.error("Unknown error during get users: $e")
            ResponseEntity.badRequest().body(UsersResponseData("Неизвестная ошибка", null))
        }
    }
}
