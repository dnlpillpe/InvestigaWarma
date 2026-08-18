package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Perfil del jugador. Único registro por instalación (id fijo = 1).
 * No se solicita nombre real, email, teléfono ni ubicación: solo alias y avatar local.
 */
@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val alias: String,
    val avatarId: Int,
    val level: Int = 1,
    val xp: Int = 0,
    val createdAt: Long,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false,
)
