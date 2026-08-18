package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Un desafío corto (minijuego) de una zona: detective, ordenar, patrones,
 * clasificar o construir. dataJson contiene el payload específico del tipo.
 */
@Entity(
    tableName = "challenge",
    indices = [Index(value = ["zone"]), Index(value = ["type"])],
)
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val zone: String,
    val type: String,
    val title: String,
    val dataJson: String,
    val difficulty: Int,
)
