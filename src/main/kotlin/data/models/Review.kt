package org.example.data.models

import org.example.data.files.SerializableInterface
import org.example.exceptions.RestException

class Review : SerializableInterface{
    constructor(mark: Int, comment: String) {
        this.mark = mark
        this.comment = comment
    }

    private var id1: Int = 0
    private var mark1: Int = 0
    private var comment1: String = ""

    override var id: Int
        get() {
            return id1
        }
        set(value) {
            id1 = value
        }

    var mark: Int
        get() {
            return mark1
        }
        set(value) {
            if (value in 1..5) {
                mark1 = value
            } else {
                throw RestException("Оценка должна быть от 1 до 5")
            }
        }

    var comment: String
        get() {
            return comment1
        }
        set(value) {
            comment1 = value
        }

    override fun toString(): String {
        return "Review(id=$id1, mark=$mark1, comment='$comment1')"
    }
}
