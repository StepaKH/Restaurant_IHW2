package org.example.repositories

import org.example.data.files.WorkWithFiles
import org.example.data.models.User
import java.io.File

class UserRepository : WorkWithFiles<User>{
    companion object {
        private val dbRepository = System.getProperty("user.dir") + System.getProperty("file.separator") +
                "src" + System.getProperty("file.separator") +
                "main" + System.getProperty("file.separator") +
                "kotlin" + System.getProperty("file.separator") +
                "database" + System.getProperty("file.separator") +
                "users.json"
    }

    override val file = File(dbRepository)

    override fun readAll(): List<User> {
        return readFromFile(User::class.java)
    }

    override fun read(id: Int): User? {
        return readAll().firstOrNull { it.id == id }
    }

    override fun readByField(fieldName: String, fieldValue: String): User? {
        val users = readAll()
        return users.firstOrNull { it.serialize().contains("\"$fieldName\":\"$fieldValue\"") }
    }

    override fun create(obj: User): Boolean {
        val list = readAll().toMutableList()
        obj.id = list.size + 1
        list.add(obj)
        return writeToFile(list)
    }

    override fun update(obj: User): Boolean {
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