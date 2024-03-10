package org.example.components

object TokenGenerator {
    fun generateToken(tokenLength: Int): String {
        val randomToken = (1..tokenLength)
            .map { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random() }
            .joinToString("")
        return randomToken
    }
}