package org.example.exceptions

class RestException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}