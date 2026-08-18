package com.investigawarma.app.data.repository

import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.domain.model.Zone
import kotlinx.coroutines.flow.first

/** Estadísticas calculadas SIEMPRE a partir de datos persistidos, nunca escritas a mano. */
class StatsRepository(private val db: AppDatabase) {

    data class ZoneStat(val zone: Zone, val completed: Int, val total: Int)

    data class PlayerStats(
        val totalMissions: Int,
        val completedMissions: Int,
        val zoneStats: List<ZoneStat>,
        val totalChallengeAttempts: Int,
        val successfulChallengeAttempts: Int,
        val challengeAccuracyPercent: Int,
        val totalExperiments: Int,
        val totalHypotheses: Int,
        val validHypotheses: Int,
        val unlockedBadges: Int,
        val totalBadges: Int,
        val unlockedCollectionItems: Int,
        val totalCollectionItems: Int,
    )

    suspend fun computeStats(): PlayerStats {
        val totalMissions = db.scientificMissionDao().count()
        val completedMissions = db.missionProgressDao().countCompleted()

        val zoneStats = Zone.entries.map { zone ->
            val total = db.scientificMissionDao().countByZone(zone.name)
            val progress = db.missionProgressDao().observeByZone(zone.name).first()
            val completed = progress.count { it.status == "COMPLETED" || it.status == "MASTERED" }
            ZoneStat(zone, completed, total)
        }

        val totalAttempts = db.challengeDao().countTotalAttempts()
        val successfulAttempts = db.challengeDao().countSuccessfulAttempts()
        val accuracy = if (totalAttempts > 0) (successfulAttempts * 100) / totalAttempts else 0

        val totalExperiments = db.experimentResultDao().count()
        val totalHypotheses = db.hypothesisDao().countTotal()
        val validHypotheses = db.hypothesisDao().countValid()

        val unlockedBadges = db.badgeDao().countUnlocked()
        val unlockedCollection = db.collectionItemDao().countUnlocked()
        val totalCollection = db.collectionItemDao().countTotal()

        return PlayerStats(
            totalMissions = totalMissions,
            completedMissions = completedMissions,
            zoneStats = zoneStats,
            totalChallengeAttempts = totalAttempts,
            successfulChallengeAttempts = successfulAttempts,
            challengeAccuracyPercent = accuracy,
            totalExperiments = totalExperiments,
            totalHypotheses = totalHypotheses,
            validHypotheses = validHypotheses,
            unlockedBadges = unlockedBadges,
            totalBadges = 15,
            unlockedCollectionItems = unlockedCollection,
            totalCollectionItems = totalCollection,
        )
    }
}
