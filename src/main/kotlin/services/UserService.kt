package org.example.services

import org.example.components.HashPassword
import org.example.components.TokenGenerator
import org.example.data.files.WorkWithFiles
import org.example.data.models.User
import org.example.exceptions.RestException
import org.springframework.stereotype.Service


@Service
class UserService(private val userRepository: WorkWithFiles<User>) {
    private final val _tokenLength = 32

    fun register(login: String?, password: String?, isAdmin: Boolean?): String {
        if (login == null || password == null) {
            throw RestException("Переданы не все параметры")
        }
        val user = userRepository.readByField("login1", login)
        if (user != null) throw RestException("Пользователь с таким логином уже существует")
        val hashedPassword = HashPassword.hashPassword(password)
        val token = TokenGenerator.generateToken(_tokenLength)
        val newUser: User = if (isAdmin == null || isAdmin == false)
            User(login, hashedPassword, token, false)
        else
            User(login, hashedPassword, token, true)
        val isCreate = userRepository.create(newUser)
        if (!isCreate) throw RestException("Ошибка подключения к базе данных")
        return token
    }

    fun authenticate(user: User): String {
        if (user.token != null) throw RestException("Пользователь уже авторизирован")
        val newToken = TokenGenerator.generateToken(_tokenLength)
        user.token = newToken
        val isUpdate = userRepository.update(user)
        if (!isUpdate) throw RestException("Ошибка подключения к базе данных")
        return newToken
    }

    fun logout(user: User): Boolean {
        if (user.token == null) throw RestException("Пользователь не авторизирован")
        user.token = null
        val isUpdate = userRepository.update(user)
        if (!isUpdate) throw RestException("Ошибка подключения к базе данных")
        return true
    }

    fun getUsers(user: User): List<User> {
        if (!user.isAdmin) throw RestException("Пользователь не является администратором")
        val users = userRepository.readAll()
        return users
    }

    fun getUser(login: String?, password: String?): User {
        if (login == null || password == null) throw RestException("Переданы не все параметры")
        val user = userRepository.readByField("login1", login)
            ?: throw RestException("Пользователь с таким логином не существует")
        if (!HashPassword.verifyPassword(password, user.password)) throw RestException("Неверный пароль")
        return user
    }

    fun getUser(token: String?): User {
        if (token == null) throw RestException("Передан пустой токен")
        val user = userRepository.readByField("token1", token)
            ?: throw RestException("Пользователь с таким токеном не существует")
        return user
    }
}