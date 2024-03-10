package org.example.data.files

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

interface WorkWithFiles<T : SerializableInterface> {
    val file: File
    fun readAll(): List<T>
    fun read(id: Int): T?
    fun readByField(fieldName: String, fieldValue: String): T?
    fun create(obj: T): Boolean
    fun update(obj: T): Boolean
    fun delete(id: Int): Boolean

    fun readFromFile(clazz: Class<T>): List<T> {
        if (!file.exists() || file.length() == 0L) {
            return emptyList()
        }
        val listType = TypeToken.getParameterized(List::class.java, clazz).type
        val content = file.readText()
        return Gson().fromJson(content, listType)
    }

    fun writeToFile(list: List<T>): Boolean {
        val text = Gson().toJson(list)
        file.writeText(text)
        return true
    }
}