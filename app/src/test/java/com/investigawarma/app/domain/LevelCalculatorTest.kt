package com.investigawarma.app.domain

import com.investigawarma.app.domain.logic.LevelCalculator
import com.investigawarma.app.domain.model.PlayerLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class LevelCalculatorTest {

    @Test
    fun `zero xp is Explorador Cientifico`() {
        assertEquals(PlayerLevel.EXPLORADOR_CIENTIFICO, LevelCalculator.levelFor(0))
    }

    @Test
    fun `just below threshold stays at previous level`() {
        assertEquals(PlayerLevel.EXPLORADOR_CIENTIFICO, LevelCalculator.levelFor(299))
    }

    @Test
    fun `exact threshold advances level`() {
        assertEquals(PlayerLevel.INVESTIGADOR_JUNIOR, LevelCalculator.levelFor(300))
    }

    @Test
    fun `800 xp is Cientifico en Formacion`() {
        assertEquals(PlayerLevel.CIENTIFICO_EN_FORMACION, LevelCalculator.levelFor(800))
    }

    @Test
    fun `1600 xp is Gran Descubridor`() {
        assertEquals(PlayerLevel.GRAN_DESCUBRIDOR, LevelCalculator.levelFor(1600))
    }

    @Test
    fun `negative xp is treated as zero (edge case)`() {
        assertEquals(PlayerLevel.EXPLORADOR_CIENTIFICO, LevelCalculator.levelFor(-500))
    }

    @Test
    fun `progress at zero xp is zero percent`() {
        assertEquals(0, LevelCalculator.progressToNextLevel(0))
    }

    @Test
    fun `progress halfway between levels is fifty percent`() {
        assertEquals(50, LevelCalculator.progressToNextLevel(150))
    }

    @Test
    fun `progress at max level is one hundred percent`() {
        assertEquals(100, LevelCalculator.progressToNextLevel(1600))
    }

    @Test
    fun `progress beyond max level stays at one hundred percent (edge case)`() {
        assertEquals(100, LevelCalculator.progressToNextLevel(999_999))
    }

    @Test
    fun `xp to next level is zero at max level`() {
        assertEquals(0, LevelCalculator.xpToNextLevel(1600))
    }

    @Test
    fun `xp to next level counts down correctly`() {
        assertEquals(300, LevelCalculator.xpToNextLevel(0))
        assertEquals(150, LevelCalculator.xpToNextLevel(150))
    }

    @Test
    fun `level index ordering matches progression`() {
        assertEquals(1, LevelCalculator.levelIndex(PlayerLevel.EXPLORADOR_CIENTIFICO))
        assertEquals(2, LevelCalculator.levelIndex(PlayerLevel.INVESTIGADOR_JUNIOR))
        assertEquals(3, LevelCalculator.levelIndex(PlayerLevel.CIENTIFICO_EN_FORMACION))
        assertEquals(4, LevelCalculator.levelIndex(PlayerLevel.GRAN_DESCUBRIDOR))
    }

    @Test
    fun `next level after max level is null`() {
        assertEquals(null, PlayerLevel.next(PlayerLevel.GRAN_DESCUBRIDOR))
    }
}
