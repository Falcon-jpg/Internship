package com.example.internship.retrofit

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Retrofit {

    companion object {
        private const val BASE_URL = "http://192.168.162.175:8080"

        private val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .setLenient()
            .create()

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        fun <T> create(service: Class<T>): T {
            return retrofit.create(service)
        }
    }

    class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>{
        override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(src?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        }
    }
        class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
                val jsonArray = json.asJsonArray
                if (jsonArray.size() != 7) {
                    throw JsonParseException("Expected array of size 7 for LocalDateTime, but got ${jsonArray.size()}")
                }

                val year = jsonArray[0].asInt
                val month = jsonArray[1].asInt
                val day = jsonArray[2].asInt
                val hour = jsonArray[3].asInt
                val minute = jsonArray[4].asInt
                val second = jsonArray[5].asInt
                val nanoOfSecond = jsonArray[6].asInt

                return LocalDateTime.of(year, month, day, hour, minute, second, nanoOfSecond)
            }
        }
}
