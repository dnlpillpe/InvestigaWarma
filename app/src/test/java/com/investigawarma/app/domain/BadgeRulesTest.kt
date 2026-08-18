package com.investigawarma.app.domain

import com.investigawarma.app.domain.logic.BadgeRules
import com.investigawarma.app.domain.model.BadgeKeys
import com.investigawarma.app.domain.model.PlayerLevel
import com.investigawarma.app.domain.model.Zone
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgeRulesTest {

    private fun emptySnapshot() = BadgeRules.ProgressSnapshot(
        totalMissionsCompleted = 0,
        zonesFullyCompleted = emptySet(),
        validHypothesesCount = 0,
        successfulDetectiveStreak = 0,
        successfulOrderCount = 0,
        successfulPatternCount = 0,
        successfulClassifyCount = 0,
        journalEntryCount = 0,
        voiceEntryCount = 0,
        unlockedCollectionCount = 0,
        playerLevel = PlayerLevel.EXPLORADOR_CIENTIFICO,
    )

    @Test
    fun `brand new player has no badges unlocked (edge case)`() {
        val result = BadgeRules.evaluate(emptySnapshot())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `first completed mission unlocks primeros pasos`() {
        val snapshot = emptySnapshot().copy(totalMissionsCompleted = 1)
        assertTrue(BadgeRules.evaluate(snapshot).contains(BadgeKeys.PRIMEROS_PASOS))
    }

    @Test
    fun `completing a zone unlocks its badge`() {
        val snapshot = emptySnapshot().copy(zonesFullyCompleted = setOf(Zone.SALA_OBSERVACION))
        assertTrue(BadgeRules.evaluate(snapshot).contains(BadgeKeys.OBSERVADOR_AGUDO))
    }

    @Test
    fun `nine valid hypotheses does not yet unlock constructor badge (edge case)`() {
        val snapshot = emptySnapshot().copy(validHypothesesCount = 9)
        assertFalse(BadgeRules.evaluate(snapshot).contains(BadgeKeys.CONSTRUCTOR_HIPOTESIS))
    }

    @Test
    fun `ten valid hypotheses unlocks constructor badge`() {
        val snapshot = emptySnapshot().copy(validHypothesesCount = 10)
        assertTrue(BadgeRules.evaluate(snapshot).contains(BadgeKeys.CONSTRUCTOR_HIPOTESIS))
    }

    @Test
    fun `five successful detective streak unlocks racha detective`() {
        val snapshot = emptySnapshot().copy(successfulDetectiveStreak = 5)
        assertTrue(BadgeRules.evaluate(snapshot).contains(BadgeKeys.RACHA_DETECTIVE))
    }

    @Test
    fun `ten journal entries unlocks diario completo`() {
        val snapshot = emptySnapshot().copy(journalEntryCount = 10)
        assertTrue(BadgeRules.evaluate(snapshot).contains(BadgeKeys.DIARIO_COMPLETO))
    }

    @Test
    fun `five voice entries unlocks voz de investigador`() {
        val snapshot = emptySnapshot().copy(voiceEntryCount = 5)
        assertTrue(BadgeRules.evaluate(snapshot).contains(BadgeKeys.VOZ_INVESTIGADOR))
    }

    @Test
    fun `ten unlocked collection items unlocks coleccionista`() {
        val snapshot = emptySnapshot().copy(unlockedCollectionCount = 10)
        assertTrue(BadgeRules.evaluate(snapshot).contains(BadgeKeys.COLECCIONISTA))
    }

    @Test
    fun `reaching max level unlocks gran descubridor`() {
        val snapshot = emptySnapshot().copy(playerLevel = PlayerLevel.GRAN_DESCUBRIDOR)
        assertTrue(BadgeRules.evaluate(snapshot).contains(BadgeKeys.GRAN_DESCUBRIDOR))
    }

    @Test
    fun `intermediate level does not unlock gran descubridor (edge case)`() {
        val snapshot = emptySnapshot().copy(playerLevel = PlayerLevel.CIENTIFICO_EN_FORMACION)
        assertFalse(BadgeRules.evaluate(snapshot).contains(BadgeKeys.GRAN_DESCUBRIDOR))
    }

    @Test
    fun `all zones completed unlocks all zone badges at once`() {
        val snapshot = emptySnapshot().copy(zonesFullyCompleted = Zone.entries.toSet())
        val result = BadgeRules.evaluate(snapshot)
        assertTrue(result.contains(BadgeKeys.OBSERVADOR_AGUDO))
        assertTrue(result.contains(BadgeKeys.CIENTIFICO_LABORATORIO))
        assertTrue(result.contains(BadgeKeys.AMIGO_NATURALEZA))
        assertTrue(result.contains(BadgeKeys.GUARDIAN_PLANETA))
        assertTrue(result.contains(BadgeKeys.ANALISTA_DATOS))
    }
}
