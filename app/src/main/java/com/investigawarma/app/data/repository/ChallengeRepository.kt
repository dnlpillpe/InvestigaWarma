package com.investigawarma.app.data.repository

import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.data.local.entity.ChallengeAttemptEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ChallengeRepository(private val db: AppDatabase) {

    fun observeByZone(zone: String) = db.challengeDao().observeByZone(zone)

    suspend fun getChallenge(id: String) = db.challengeDao().getById(id)

    fun observeAttempts(challengeId: String): Flow<List<ChallengeAttemptEntity>> =
        db.challengeDao().observeAttempts(challengeId)

    suspend fun recordAttempt(challengeId: String, success: Boolean, score: Float) {
        db.challengeDao().insertAttempt(
            ChallengeAttemptEntity(
                challengeId = challengeId,
                success = success,
                score = score,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    /** Racha actual de éxitos consecutivos para desafíos del tipo dado (ej: DETECTIVE). */
    suspend fun currentSuccessStreak(type: String): Int {
        val attempts = db.challengeDao().observeAllAttempts().first()
        // El tipo no está desnormalizado en ChallengeAttemptEntity, así que se resuelve
        // consultando el desafío relacionado de cada intento (los intentos son pocos por diseño).
        var streak = 0
        for (attempt in attempts) {
            val challenge = db.challengeDao().getById(attempt.challengeId) ?: continue
            if (challenge.type != type) continue
            if (attempt.success) streak++ else break
        }
        return streak
    }

    suspend fun successfulCountByType(type: String): Int {
        val attempts = db.challengeDao().observeAllAttempts().first()
        return attempts.count { attempt ->
            attempt.success && db.challengeDao().getById(attempt.challengeId)?.type == type
        }
    }

    suspend fun getPendingReview(limit: Int = 5) = db.challengeDao().getPendingReview(limit)
}
