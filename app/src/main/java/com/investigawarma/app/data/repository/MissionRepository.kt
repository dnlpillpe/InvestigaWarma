package com.investigawarma.app.data.repository

import androidx.room.withTransaction
import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.data.local.entity.HypothesisEntity
import com.investigawarma.app.data.local.entity.ScientificMissionEntity
import com.investigawarma.app.domain.logic.HypothesisValidator
import com.investigawarma.app.domain.model.MissionStatus
import com.investigawarma.app.domain.model.Zone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MissionRepository(private val db: AppDatabase) {

    fun observeMissionsByZone(zone: Zone): Flow<List<ScientificMissionEntity>> =
        db.scientificMissionDao().observeByZone(zone.name)

    fun observeAllMissions(): Flow<List<ScientificMissionEntity>> =
        db.scientificMissionDao().observeAll()

    fun observeSteps(missionId: String) = db.missionStepDao().observeByMission(missionId)

    fun observeProgress(missionId: String) = db.missionProgressDao().observeByMission(missionId)

    fun observeAllProgress() = db.missionProgressDao().observeAll()

    suspend fun getMission(missionId: String): ScientificMissionEntity? =
        db.scientificMissionDao().getById(missionId)

    suspend fun startMission(missionId: String) {
        val current = db.missionProgressDao().getByMission(missionId) ?: return
        if (current.status == MissionStatus.AVAILABLE.name) {
            db.missionProgressDao().update(
                current.copy(status = MissionStatus.STARTED.name, lastAttemptAt = System.currentTimeMillis()),
            )
        }
    }

    /** Guarda una hipótesis del jugador, validando su estructura mínima. */
    suspend fun submitHypothesis(
        missionId: String,
        variableText: String,
        resultText: String,
        explanationText: String,
    ): HypothesisValidator.ValidationResult {
        val result = HypothesisValidator.validate(variableText, resultText, explanationText)
        db.hypothesisDao().insert(
            HypothesisEntity(
                missionId = missionId,
                variableText = variableText.trim(),
                resultText = resultText.trim(),
                explanationText = explanationText.trim(),
                isValidStructure = result.isValid,
                createdAt = System.currentTimeMillis(),
            ),
        )
        return result
    }

    /**
     * Marca una misión como completada (o dominada si el score es alto), desbloquea
     * la siguiente misión de la zona y su descubrimiento asociado. Se ejecuta dentro
     * de una transacción Room para mantener el estado consistente ante fallos.
     */
    suspend fun completeMission(missionId: String, score: Float) {
        db.withTransaction {
            val progress = db.missionProgressDao().getByMission(missionId) ?: return@withTransaction
            val newStatus = if (score >= 0.9f) MissionStatus.MASTERED else MissionStatus.COMPLETED
            val now = System.currentTimeMillis()
            db.missionProgressDao().update(
                progress.copy(
                    status = newStatus.name,
                    attempts = progress.attempts + 1,
                    bestScore = maxOf(progress.bestScore, score),
                    completedAt = progress.completedAt ?: now,
                    lastAttemptAt = now,
                ),
            )
            db.scientificDiscoveryDao().unlockForMission(missionId, now)

            val mission = db.scientificMissionDao().getById(missionId)
            if (mission != null) {
                val zoneMissions = db.scientificMissionDao().observeByZone(mission.zone).first()
                val next = zoneMissions.firstOrNull { it.requiredMissionId == missionId }
                if (next != null) {
                    val nextProgress = db.missionProgressDao().getByMission(next.id)
                    if (nextProgress != null && nextProgress.status == MissionStatus.LOCKED.name) {
                        db.missionProgressDao().update(nextProgress.copy(status = MissionStatus.AVAILABLE.name))
                    }
                }
            }
        }
    }

    suspend fun missionsCompletedCount(): Int = db.missionProgressDao().countCompleted()
}
