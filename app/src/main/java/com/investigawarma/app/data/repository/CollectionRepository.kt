package com.investigawarma.app.data.repository

import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.domain.logic.BadgeRules
import com.investigawarma.app.domain.logic.LevelCalculator
import com.investigawarma.app.domain.model.Zone
import kotlinx.coroutines.flow.first

/**
 * Orquesta el Museo Científico Personal: coleccionables e insignias, y la
 * lógica de "revisar y desbloquear lo que corresponda" tras cada acción del jugador.
 */
class CollectionRepository(private val db: AppDatabase) {

    fun observeCollectionItems() = db.collectionItemDao().observeAll()
    fun observeUnlockedCollectionItems() = db.collectionItemDao().observeUnlocked()
    fun observeBadges() = db.badgeDao().observeAll()
    fun observeUnlockedBadges() = db.badgeDao().observeUnlocked()

    /**
     * Recalcula qué insignias corresponden según el progreso actual y desbloquea
     * las que falten. Se llama tras completar misiones, hipótesis, experimentos o desafíos.
     */
    suspend fun refreshBadges() {
        val snapshot = buildSnapshot()
        val shouldBeUnlocked = BadgeRules.evaluate(snapshot)
        for (key in shouldBeUnlocked) {
            db.badgeDao().unlockByKey(key, System.currentTimeMillis())
        }
        refreshCollectionUnlocks(snapshot)
    }

    private suspend fun buildSnapshot(): BadgeRules.ProgressSnapshot {
        val profile = db.playerProfileDao().getProfile()
        val completedMissions = db.missionProgressDao().countCompleted()
        val zonesCompleted = Zone.entries.filter { zone ->
            val zoneProgress = db.missionProgressDao().observeByZone(zone.name).first()
            zoneProgress.isNotEmpty() && zoneProgress.all { it.status == "COMPLETED" || it.status == "MASTERED" }
        }.toSet()
        val validHypotheses = db.hypothesisDao().countValid()
        val detectiveStreak = streakForType("DETECTIVE")
        val orderCount = successCountForType("ORDER")
        val patternCount = successCountForType("PATTERN")
        val classifyCount = successCountForType("CLASSIFY")
        val journalCount = db.scientificJournalDao().count()
        val voiceCount = db.voiceEntryDao().observeAll().first().size
        val unlockedCollection = db.collectionItemDao().countUnlocked()
        val level = LevelCalculator.levelFor(profile?.xp ?: 0)

        return BadgeRules.ProgressSnapshot(
            totalMissionsCompleted = completedMissions,
            zonesFullyCompleted = zonesCompleted,
            validHypothesesCount = validHypotheses,
            successfulDetectiveStreak = detectiveStreak,
            successfulOrderCount = orderCount,
            successfulPatternCount = patternCount,
            successfulClassifyCount = classifyCount,
            journalEntryCount = journalCount,
            voiceEntryCount = voiceCount,
            unlockedCollectionCount = unlockedCollection,
            playerLevel = level,
        )
    }

    private suspend fun streakForType(type: String): Int {
        val attempts = db.challengeDao().observeAllAttempts().first()
        var streak = 0
        for (attempt in attempts) {
            val challenge = db.challengeDao().getById(attempt.challengeId) ?: continue
            if (challenge.type != type) continue
            if (attempt.success) streak++ else break
        }
        return streak
    }

    private suspend fun successCountForType(type: String): Int {
        val attempts = db.challengeDao().observeAllAttempts().first()
        return attempts.count { it.success && db.challengeDao().getById(it.challengeId)?.type == type }
    }

    private suspend fun refreshCollectionUnlocks(snapshot: BadgeRules.ProgressSnapshot) {
        val now = System.currentTimeMillis()
        // Iniciales
        if (snapshot.totalMissionsCompleted >= 1) db.collectionItemDao().unlockByKey("lupa", now)
        if (snapshot.journalEntryCount >= 1) db.collectionItemDao().unlockByKey("cuaderno", now)
        if (db.experimentResultDao().count() >= 1) db.collectionItemDao().unlockByKey("regla", now)
        if (Zone.SALA_OBSERVACION in snapshot.zonesFullyCompleted) db.collectionItemDao().unlockByKey("microscopio", now)
        // Intermedios
        if (Zone.LABORATORIO_EXPERIMENTAL in snapshot.zonesFullyCompleted) db.collectionItemDao().unlockByKey("robot", now)
        if (db.experimentResultDao().count() >= 5) db.collectionItemDao().unlockByKey("sensores", now)
        if (Zone.BIODESCUBRIMIENTO in snapshot.zonesFullyCompleted) db.collectionItemDao().unlockByKey("brujula", now)
        if (snapshot.successfulClassifyCount + snapshot.successfulOrderCount + snapshot.successfulPatternCount >= 3) {
            db.collectionItemDao().unlockByKey("balanza", now)
        }
        if (snapshot.validHypothesesCount >= 5) db.collectionItemDao().unlockByKey("iman", now)
        if (Zone.CENTRO_DE_DATOS in snapshot.zonesFullyCompleted) db.collectionItemDao().unlockByKey("prisma", now)
        // Avanzados
        if (snapshot.playerLevel.minXp >= com.investigawarma.app.domain.model.PlayerLevel.CIENTIFICO_EN_FORMACION.minXp) {
            db.collectionItemDao().unlockByKey("satelite", now)
        }
        if (snapshot.totalMissionsCompleted >= 20) db.collectionItemDao().unlockByKey("laboratorio_avanzado", now)
        if (snapshot.totalMissionsCompleted >= 40) db.collectionItemDao().unlockByKey("submarino", now)
        if (snapshot.playerLevel == com.investigawarma.app.domain.model.PlayerLevel.GRAN_DESCUBRIDOR) {
            db.collectionItemDao().unlockByKey("cohete", now)
        }
    }
}
