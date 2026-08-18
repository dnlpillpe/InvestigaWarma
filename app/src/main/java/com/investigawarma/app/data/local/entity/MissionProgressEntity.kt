package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Progreso persistente de una misión concreta (relación 1:1 con ScientificMissionEntity). */
@Entity(
    tableName = "mission_progress",
    foreignKeys = [
        ForeignKey(
            entity = ScientificMissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["missionId"], unique = true), Index(value = ["status"])],
)
data class MissionProgressEntity(
    @PrimaryKey val missionId: String,
    val status: String,
    val attempts: Int = 0,
    val bestScore: Float = 0f,
    val completedAt: Long? = null,
    val lastAttemptAt: Long? = null,
)
