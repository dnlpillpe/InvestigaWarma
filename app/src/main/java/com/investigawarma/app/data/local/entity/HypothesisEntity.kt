package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Hipótesis construida por el niño con el formato SI / ENTONCES / PORQUE.
 * Se persiste realmente: forma parte del diario científico del jugador.
 */
@Entity(
    tableName = "hypothesis",
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
data class HypothesisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val missionId: String,
    val variableText: String,
    val resultText: String,
    val explanationText: String,
    val isValidStructure: Boolean,
    val createdAt: Long,
)
