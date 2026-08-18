package com.investigawarma.app.data.repository

import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.data.local.entity.PlayerProfileEntity
import com.investigawarma.app.domain.logic.LevelCalculator
import kotlinx.coroutines.flow.Flow

class PlayerRepository(private val db: AppDatabase) {

    fun observeProfile(): Flow<PlayerProfileEntity?> = db.playerProfileDao().observeProfile()

    suspend fun getProfile(): PlayerProfileEntity? = db.playerProfileDao().getProfile()

    suspend fun createProfile(alias: String, avatarId: Int) {
        val safeAlias = alias.trim().ifBlank { "Investigador" }.take(20)
        db.playerProfileDao().upsert(
            PlayerProfileEntity(
                alias = safeAlias,
                avatarId = avatarId,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun completeOnboarding() = db.playerProfileDao().markOnboardingCompleted()

    suspend fun setSoundEnabled(enabled: Boolean) = db.playerProfileDao().setSoundEnabled(enabled)

    suspend fun setHapticsEnabled(enabled: Boolean) = db.playerProfileDao().setHapticsEnabled(enabled)

    /** Suma XP y recalcula el nivel derivado. Devuelve el nuevo total de XP. */
    suspend fun addXp(amount: Int): Int {
        val profile = db.playerProfileDao().getProfile() ?: return 0
        val safeAmount = amount.coerceAtLeast(0)
        val newXp = profile.xp + safeAmount
        val newLevel = LevelCalculator.levelIndex(LevelCalculator.levelFor(newXp))
        db.playerProfileDao().addXp(safeAmount, newLevel)
        return newXp
    }

    suspend fun resetProgress() = db.playerProfileDao().clear()
}
