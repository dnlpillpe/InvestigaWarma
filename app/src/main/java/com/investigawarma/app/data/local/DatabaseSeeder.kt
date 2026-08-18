package com.investigawarma.app.data.local

import com.investigawarma.app.data.local.entity.MissionProgressEntity
import com.investigawarma.app.data.local.seed.SeedBadges
import com.investigawarma.app.data.local.seed.SeedChallenges
import com.investigawarma.app.data.local.seed.SeedCollectionItems
import com.investigawarma.app.data.local.seed.SeedDiscoveries
import com.investigawarma.app.data.local.seed.SeedExperiments
import com.investigawarma.app.data.local.seed.SeedMissionSteps
import com.investigawarma.app.data.local.seed.SeedMissions
import com.investigawarma.app.domain.model.MissionStatus

/**
 * Puebla la base de datos vacía con el contenido inicial de InvestigaWarma:
 * 40 misiones, 240 pasos de misión, 40 descubrimientos, 30 experimentos con
 * sus parámetros, 50 desafíos, 20 coleccionables y 15 insignias.
 *
 * Se ejecuta una única vez, la primera vez que la app abre la base de datos
 * (ver InvestigaWarmaApp). Es idempotente: si ya hay misiones, no repite el seed.
 */
class DatabaseSeeder(private val db: AppDatabase) {

    suspend fun seedIfNeeded() {
        val alreadySeeded = db.scientificMissionDao().count() > 0
        if (alreadySeeded) return

        seedMissionsAndProgress()
        db.missionStepDao().insertAll(SeedMissionSteps.ALL)
        db.scientificDiscoveryDao().insertAll(SeedDiscoveries.ALL)
        db.experimentDao().insertWithParameters(SeedExperiments.ALL, SeedExperiments.PARAMETERS)
        db.challengeDao().insertAll(SeedChallenges.ALL)
        db.collectionItemDao().insertAll(SeedCollectionItems.ALL)
        db.badgeDao().insertAll(SeedBadges.ALL)
    }

    private suspend fun seedMissionsAndProgress() {
        db.scientificMissionDao().insertAll(SeedMissions.ALL)

        // La primera misión de cada zona empieza DISPONIBLE; el resto, BLOQUEADA
        // hasta que se complete la misión previa (progresión real, no decorativa).
        val progress = SeedMissions.ALL.map { mission ->
            val status = if (mission.requiredMissionId == null) {
                MissionStatus.AVAILABLE
            } else {
                MissionStatus.LOCKED
            }
            MissionProgressEntity(missionId = mission.id, status = status.name)
        }
        db.missionProgressDao().insertAll(progress)
    }
}
