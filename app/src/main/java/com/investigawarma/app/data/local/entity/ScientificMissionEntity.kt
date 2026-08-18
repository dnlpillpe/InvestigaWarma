package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Una misión científica: la unidad principal de contenido educativo.
 * Cada misión pertenece a una zona y sigue el ciclo de juego:
 * observar -> preguntar -> hipotetizar -> experimentar -> analizar -> descubrir.
 */
@Entity(
    tableName = "scientific_mission",
    indices = [Index(value = ["zone", "orderIndex"]), Index(value = ["requiredMissionId"])],
)
data class ScientificMissionEntity(
    @PrimaryKey val id: String,
    val zone: String,
    val orderIndex: Int,
    val title: String,
    val story: String,
    val objective: String,
    val mechanicType: String,
    val difficulty: Int,
    val xpReward: Int,
    val starReward: Int,
    /** Palabras clave del tema científico (ej: ["luz", "crecimiento"]). Usa Converters para persistirse como texto. */
    val tags: List<String> = emptyList(),
    /** Misión previa requerida dentro de la misma zona, o null si es la primera. */
    val requiredMissionId: String? = null,
)
