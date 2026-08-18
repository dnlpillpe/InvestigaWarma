package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Resultado real de una ejecución de experimento: los parámetros elegidos por el niño
 * y el resultado calculado por el motor del simulador (no texto escrito a mano).
 */
@Entity(
    tableName = "experiment_result",
    foreignKeys = [
        ForeignKey(
            entity = ExperimentEntity::class,
            parentColumns = ["id"],
            childColumns = ["experimentId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["experimentId"])],
)
data class ExperimentResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val experimentId: String,
    /** Valores elegidos por el jugador para cada parámetro. Persistido vía Converters. */
    val parameters: Map<String, Float>,
    val outcomeSummary: String,
    val outcomeScore: Float,
    val createdAt: Long,
)
