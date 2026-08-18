package com.investigawarma.app.domain.logic

import com.investigawarma.app.domain.model.BadgeKeys
import com.investigawarma.app.domain.model.PlayerLevel
import com.investigawarma.app.domain.model.Zone

/**
 * Reglas puras de desbloqueo de insignias. Todas las condiciones se basan en
 * contadores reales derivados de la base de datos (nunca en texto fijo).
 */
object BadgeRules {

    data class ProgressSnapshot(
        val totalMissionsCompleted: Int,
        val zonesFullyCompleted: Set<Zone>,
        val validHypothesesCount: Int,
        val successfulDetectiveStreak: Int,
        val successfulOrderCount: Int,
        val successfulPatternCount: Int,
        val successfulClassifyCount: Int,
        val journalEntryCount: Int,
        val voiceEntryCount: Int,
        val unlockedCollectionCount: Int,
        val playerLevel: PlayerLevel,
    )

    /** Devuelve las claves de insignias que deberían estar desbloqueadas dado este progreso. */
    fun evaluate(snapshot: ProgressSnapshot): Set<String> {
        val unlocked = mutableSetOf<String>()

        if (snapshot.totalMissionsCompleted >= 1) unlocked += BadgeKeys.PRIMEROS_PASOS
        if (Zone.SALA_OBSERVACION in snapshot.zonesFullyCompleted) unlocked += BadgeKeys.OBSERVADOR_AGUDO
        if (Zone.LABORATORIO_EXPERIMENTAL in snapshot.zonesFullyCompleted) unlocked += BadgeKeys.CIENTIFICO_LABORATORIO
        if (Zone.BIODESCUBRIMIENTO in snapshot.zonesFullyCompleted) unlocked += BadgeKeys.AMIGO_NATURALEZA
        if (Zone.PLANETA_TIERRA in snapshot.zonesFullyCompleted) unlocked += BadgeKeys.GUARDIAN_PLANETA
        if (Zone.CENTRO_DE_DATOS in snapshot.zonesFullyCompleted) unlocked += BadgeKeys.ANALISTA_DATOS

        if (snapshot.validHypothesesCount >= 10) unlocked += BadgeKeys.CONSTRUCTOR_HIPOTESIS
        if (snapshot.successfulDetectiveStreak >= 5) unlocked += BadgeKeys.RACHA_DETECTIVE
        if (snapshot.successfulOrderCount >= 10) unlocked += BadgeKeys.ORDENADOR_EXPERTO
        if (snapshot.successfulPatternCount >= 10) unlocked += BadgeKeys.CAZADOR_PATRONES
        if (snapshot.successfulClassifyCount >= 10) unlocked += BadgeKeys.CLASIFICADOR_MAESTRO

        if (snapshot.journalEntryCount >= 10) unlocked += BadgeKeys.DIARIO_COMPLETO
        if (snapshot.voiceEntryCount >= 5) unlocked += BadgeKeys.VOZ_INVESTIGADOR
        if (snapshot.unlockedCollectionCount >= 10) unlocked += BadgeKeys.COLECCIONISTA
        if (snapshot.playerLevel == PlayerLevel.GRAN_DESCUBRIDOR) unlocked += BadgeKeys.GRAN_DESCUBRIDOR

        return unlocked
    }
}
