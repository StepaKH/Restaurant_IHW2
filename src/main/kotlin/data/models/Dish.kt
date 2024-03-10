package org.example.data.models

import org.example.data.files.SerializableInterface

class Dish : SerializableInterface{
    constructor(name: String, description: String, quantity: Int, cookingTimeMinutes: Int, price: Double) {
        this.name = name
        this.description = description
        this.quantity = quantity
        this.cookingTimeMinutes = cookingTimeMinutes
        this.price = price
    }

    private var name1: String = ""
    private var description1: String = ""
    private var quantity1: Int = 0
    private var cookingTimeMinutes1: Int = 0
    private var price1: Double = 0.0
    private var id1: Int = 0

    var name: String
        get() {
            return name1
        }
        set(value) {
            name1 = value
        }
    var description: String
        get() {
            return description1
        }
        set(value) {
            description1 = value
        }
    var quantity: Int
        get() {
            return quantity1
        }
        set(value) {
            quantity1 = value
        }
    var cookingTimeMinutes: Int
        get() {
            return cookingTimeMinutes1
        }
        set(value) {
            cookingTimeMinutes1 = value
        }
    var price: Double
        get() {
            return price1
        }
        set(value) {
            price1 = value
        }

    override var id: Int
        get() {
            return id1
        }
        set(value) {
            id1 = value
        }

    override fun toString(): String {
        return "Dish(id=$id1, name=$name1, description=$description1, quantity=$quantity1, cookingTime=$cookingTimeMinutes1, price=$price1)"
    }
}