package com.investigawarma.app.domain

import com.investigawarma.app.domain.logic.MovementSimulatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovementSimulatorEngineTest {

    @Test
    fun `zero force never moves the object (edge case)`() {
        val result = MovementSimulatorEngine.simulate(surfaceIndex = 1, weightGrams = 100f, forceNewtons = 0f)
        assertEquals(0f, result.distanceMeters, 0.01f)
        assertTrue(result.summary.contains("Sin fuerza"))
    }

    @Test
    fun `ice has less friction than carpet so it travels farther`() {
        val ice = MovementSimulatorEngine.simulate(surfaceIndex = 0, weightGrams = 5000f, forceNewtons = 5f)
        val carpet = MovementSimulatorEngine.simulate(surfaceIndex = 2, weightGrams = 5000f, forceNewtons = 5f)
        assertTrue(ice.distanceMeters > carpet.distanceMeters)
    }

    @Test
    fun `heavier objects travel less distance for the same force`() {
        val light = MovementSimulatorEngine.simulate(surfaceIndex = 1, weightGrams = 100f, forceNewtons = 1f)
        val heavy = MovementSimulatorEngine.simulate(surfaceIndex = 1, weightGrams = 400f, forceNewtons = 1f)
        assertTrue(light.distanceMeters > heavy.distanceMeters)
    }

    @Test
    fun `more force means more distance for the same surface and weight`() {
        val weak = MovementSimulatorEngine.simulate(surfaceIndex = 1, weightGrams = 200f, forceNewtons = 1f)
        val strong = MovementSimulatorEngine.simulate(surfaceIndex = 1, weightGrams = 200f, forceNewtons = 5f)
        assertTrue(strong.distanceMeters > weak.distanceMeters)
    }

    @Test
    fun `negative weight is coerced to a safe minimum and never crashes (edge case)`() {
        val result = MovementSimulatorEngine.simulate(surfaceIndex = 1, weightGrams = -50f, forceNewtons = 10f)
        assertTrue(result.distanceMeters in 0f..50f)
    }

    @Test
    fun `out of range surface index is clamped instead of crashing (edge case)`() {
        val result = MovementSimulatorEngine.simulate(surfaceIndex = 99, weightGrams = 100f, forceNewtons = 5f)
        assertTrue(result.distanceMeters in 0f..50f)
    }

    @Test
    fun `distance is always capped at fifty meters (edge case)`() {
        val result = MovementSimulatorEngine.simulate(surfaceIndex = 0, weightGrams = 1f, forceNewtons = 50f)
        assertEquals(50f, result.distanceMeters, 0.01f)
    }
}
