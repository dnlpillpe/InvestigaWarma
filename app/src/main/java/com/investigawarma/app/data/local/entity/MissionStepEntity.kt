package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Un paso dentro del flujo de una misión (observar, preguntar, hipotetizar,
 * experimentar, analizar, descubrir). contentJson lleva el payload específico
 * de cada tipo de paso (opciones de tarjetas, referencia a experimento, patrón, etc.)
 */
@Entity(
    tableName = "mission_step",
    foreignKeys = [
        ForeignKey(
            entity = ScientificMissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["missionId"])],
)
data class MissionStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val missionId: String,
    val orderIndex: Int,
    val stepType: String,
    val promptText: String,
    val contentJson: String = "{}",
)
