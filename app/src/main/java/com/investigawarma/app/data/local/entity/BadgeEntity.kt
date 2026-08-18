package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Insignia otorgada por logros (ej: completar zona, racha de hipótesis correctas). */
@Entity(
    tableName = "badge",
    indices = [Index(value = ["key"], unique = true)],
)
data class BadgeEntity(
    @PrimaryKey val id: String,
    val key: String,
    val name: String,
    val description: String,
    val category: String,
    val unlockedAt: Long? = null,
)
