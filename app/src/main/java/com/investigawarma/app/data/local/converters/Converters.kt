package com.investigawarma.app.data.local.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Convertidores Room para tipos que SQLite no soporta nativamente:
 * listas de texto (tags) y mapas de parámetros de experimento.
 */
class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else json.decodeFromString(value)

    @TypeConverter
    fun fromFloatMap(value: Map<String, Float>): String = json.encodeToString(value)

    @TypeConverter
    fun toFloatMap(value: String): Map<String, Float> =
        if (value.isBlank()) emptyMap() else json.decodeFromString(value)
}
