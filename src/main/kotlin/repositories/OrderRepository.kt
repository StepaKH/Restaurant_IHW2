package org.example.repositories

import org.example.data.files.WorkWithFiles
import org.example.data.models.Order
import java.io.File

class OrderRepository : WorkWithFiles<Order>{
    companion object {
        private val dbRepository = System.getProperty("user.dir") + System.getProperty("file.separator") +
                "src" + System.getProperty("file.separator") +
                "main" + System.getProperty("file.separator") +
                "kotlin" + System.getProperty("file.separator") +
                "database" + System.getProperty("file.separator") +
                "orders.json"
    }

    override val file = File(dbRepository)

    override fun readAll(): List<Order> {
        return readFromFile(Order::class.java)
    }

    override fun read(id: Int): Order? {
        return readAll().firstOrNull { it.id == id }
    }

    override fun readByField(fieldName: String, fieldValue: String): Order? {
        val orders = readAll()
        return orders.firstOrNull { it.serialize().contains("\"$fieldName\":\"$fieldValue\"") }
    }

    override fun create(obj: Order): Boolean {
        val list = readAll().toMutableList()
        obj.id = list.size + 1
        list.add(obj)
        return writeToFile(list)
    }

    override fun update(obj: Order): Boolean {
        val list = readAll().toMutableList()
        val existingObj = list.find { it.id == obj.id }
        existingObj?.let {
            list.remove(it)
            list.add(obj)
            return writeToFile(list)
        }
        return false
    }

    override fun delete(id: Int): Boolean {
        val list = readAll().toMutableList()
        val objToDelete = list.find { it.id == id }
        objToDelete?.let {
            list.remove(it)
            return writeToFile(list)
        }
        return false
    }
}