package com.investigawarma.app.domain

import com.investigawarma.app.domain.logic.TemperatureSimulatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class TemperatureSimulatorEngineTest {

    @Test
    fun `high insulation keeps temperature nearly unchanged`() {
        val result = TemperatureSimulatorEngine.simulate(initialTemp = 80f, insulation = 0.9f, minutes = 30f)
        assertTrue(result.summary.contains("aislamiento"))
        assertTrue(abs(result.finalTemp - 80f) < 15f)
    }

    @Test
    fun `low insulation over a long time approaches ambient temperature`() {
        val result = TemperatureSimulatorEngine.simulate(initialTemp = 100f, insulation = 0f, minutes = 1000f)
        assertTrue(abs(result.finalTemp - 22f) < 1f)
    }

    @Test
    fun `starting at ambient temperature produces no change (edge case)`() {
        val result = TemperatureSimulatorEngine.simulate(initialTemp = 22f, insulation = 0.5f, minutes = 10f)
        assertEquals(22.0f, result.finalTemp, 0.05f)
        assertTrue(result.summary.contains("casi no cambió"))
    }

    @Test
    fun `zero minutes means no change yet (edge case)`() {
        val result = TemperatureSimulatorEngine.simulate(initialTemp = 45f, insulation = 0.3f, minutes = 0f)
        assertEquals(45.0f, result.finalTemp, 0.05f)
    }

    @Test
    fun `negative minutes are coerced to zero instead of crashing (edge case)`() {
        val result = TemperatureSimulatorEngine.simulate(initialTemp = 45f, insulation = 0.3f, minutes = -20f)
        assertEquals(45.0f, result.finalTemp, 0.05f)
    }

    @Test
    fun `cooling material trends toward ambient not away from it`() {
        val result = TemperatureSimulatorEngine.simulate(initialTemp = 90f, insulation = 0.2f, minutes = 20f)
        assertTrue(result.finalTemp < 90f)
        assertTrue(result.finalTemp > 22f)
    }

    @Test
    fun `warming material below ambient trends upward`() {
        val result = TemperatureSimulatorEngine.simulate(initialTemp = 5f, insulation = 0.2f, minutes = 20f)
        assertTrue(result.finalTemp > 5f)
        assertTrue(result.finalTemp < 22f)
    }
}
