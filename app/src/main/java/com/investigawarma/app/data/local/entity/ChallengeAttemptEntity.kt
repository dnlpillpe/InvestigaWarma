package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Historial de intentos de un desafío (para estadísticas y repaso). */
@Entity(
    tableName = "challenge_attempt",
    foreignKeys = [
        ForeignKey(
            entity = ChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["challengeId"]), Index(value = ["createdAt"])],
)
data class ChallengeAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: String,
    val success: Boolean,
    val score: Float,
    val createdAt: Long,
)
