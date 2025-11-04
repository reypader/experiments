package com.rmpader

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Person(val name: String, val age: Int)

@Serializable
data class PersonWithNullable(val name: String? = null, val age: Int? = null)

fun main() {
    try {
        // Serializing objects
        val data = Person("Rey", 23)
        val string = Json.encodeToString(data)
        println(string) // {"name":"Rey","age":23}
        // Deserializing back into objects
        println(Json.decodeFromString<Person>("{\"name\":\"Rey\",\"age\":23}"))

        println(Json {
            ignoreUnknownKeys = true
        }.decodeFromString<Person>("{\"name\":\"Rey\",\"age\":23,\"height\":180}"))

        println(Json {
            ignoreUnknownKeys = true
        }.decodeFromString<PersonWithNullable>("{\"name\":\"Rey\",\"age\":23,\"height\":180}"))

        println(Json.decodeFromString<PersonWithNullable>("{\"name\":\"Rey\"}"))

        println(Json.decodeFromString<PersonWithNullable>("{}"))
    } catch (e: Exception) {
        e.printStackTrace()
    }

}