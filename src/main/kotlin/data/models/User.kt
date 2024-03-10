package org.example.data.models

import org.example.data.files.SerializableInterface

class User : SerializableInterface{
    constructor(login: String, password: String, token: String?, isAdmin: Boolean) {
        this.login = login
        this.password = password
        this.token = token
        this.isAdmin = isAdmin
    }

    private var login1: String = ""
    private var password1: String = ""
    private var token1: String? = null
    private var isAdmin1: Boolean = false
    private var id1: Int = 0

    var login: String
        get() {
            return login1
        }
        set(value) {
            login1 = value
        }
    var password: String
        get() {
            return password1
        }
        set(value) {
            password1 = value
        }
    var token: String?
        get() {
            return token1
        }
        set(value) {
            token1 = value
        }
    var isAdmin: Boolean
        get() {
            return isAdmin1
        }
        set(value) {
            isAdmin1 = value
        }

    override var id: Int
        get() {
            return id1
        }
        set(value) {
            id1 = value
        }

    override fun toString(): String {
        return "User(id=$id1, login=$login1, password=$password1, isAdmin=$isAdmin1, token=$token1)"
    }
}
