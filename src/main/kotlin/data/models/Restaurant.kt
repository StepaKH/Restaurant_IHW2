package org.example.data.models

class Restaurant private constructor(
    val name: String,
    val address: String,
    var workers: Int = 0,
    var totalRevenue: Double = 0.0){

    companion object {
        private var instance: Restaurant? = null

        fun getInstance(): Restaurant {
            return instance ?: synchronized(this) {
                instance ?: Restaurant(
                    name = "Restaurant",
                    address = "Pokrovsky Boulevard"
                ).also { instance = it }
            }
        }
    }

    fun updateRevenue(amount: Double) {
        totalRevenue += amount
    }
}
