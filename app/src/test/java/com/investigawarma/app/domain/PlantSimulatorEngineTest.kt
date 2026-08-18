package com.investigawarma.app.domain

import com.investigawarma.app.domain.logic.PlantSimulatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantSimulatorEngineTest {

    @Test
    fun `optimal light and water produce maximum health`() {
        val result = PlantSimulatorEngine.simulate(light = 60f, water = 55f, days = 10f)
        assertEquals(100, result.healthPercent)
        assertEquals(3.5f, result.growthCm, 0.01f)
        assertTrue(result.summary.contains("perfectas"))
    }

    @Test
    fun `very low water produces wilting message regardless of health`() {
        val result = PlantSimulatorEngine.simulate(light = 60f, water = 5f, days = 10f)
        assertTrue(result.summary.contains("agua"))
        assertTrue(result.summary.contains("marchita"))
    }

    @Test
    fun `excess water produces drowning message`() {
        val result = PlantSimulatorEngine.simulate(light = 60f, water = 95f, days = 10f)
        assertTrue(result.summary.contains("ahogó"))
    }

    @Test
    fun `very low light produces photosynthesis message`() {
        val result = PlantSimulatorEngine.simulate(light = 5f, water = 55f, days = 10f)
        assertTrue(result.summary.contains("fotosíntesis"))
    }

    @Test
    fun `negative inputs are coerced and never crash (edge case)`() {
        val result = PlantSimulatorEngine.simulate(light = -10f, water = -20f, days = -5f)
        assertEquals(0f, result.growthCm, 0.01f)
        assertTrue(result.healthPercent in 0..100)
    }

    @Test
    fun `days beyond the cap are clamped to sixty (edge case)`() {
        val result = PlantSimulatorEngine.simulate(light = 60f, water = 55f, days = 100_000f)
        assertEquals(21.0f, result.growthCm, 0.05f)
    }

    @Test
    fun `zero days produce zero growth (edge case)`() {
        val result = PlantSimulatorEngine.simulate(light = 60f, water = 55f, days = 0f)
        assertEquals(0f, result.growthCm, 0.01f)
    }

    @Test
    fun `health percent is always within zero to one hundred`() {
        val extremeCombos = listOf(
            Triple(0f, 0f, 30f),
            Triple(100f, 100f, 30f),
            Triple(0f, 100f, 30f),
            Triple(100f, 0f, 30f),
        )
        extremeCombos.forEach { (light, water, days) ->
            val result = PlantSimulatorEngine.simulate(light, water, days)
            assertTrue(result.healthPercent in 0..100)
        }
    }
}
