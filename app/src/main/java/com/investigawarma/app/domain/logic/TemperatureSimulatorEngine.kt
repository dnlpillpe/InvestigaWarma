package com.investigawarma.app.domain.logic

import kotlin.math.roundToInt

/**
 * Motor real del simulador de temperatura. Modela el enfriamiento/calentamiento
 * hacia una temperatura ambiente de referencia (22°C) usando una aproximación de
 * la ley de enfriamiento de Newton, donde el "aislamiento" retrasa el cambio.
 */
object TemperatureSimulatorEngine {

    private const val AMBIENT_TEMP = 22f

    data class TemperatureResult(
        val finalTemp: Float,
        val summary: String,
    )

    fun simulate(initialTemp: Float, insulation: Float, minutes: Float): TemperatureResult {
        val safeInitial = initialTemp.coerceIn(0f, 100f)
        val safeInsulation = insulation.coerceIn(0f, 1f)
        val safeMinutes = minutes.coerceIn(0f, 120f)

        // A mayor aislamiento, menor velocidad de cambio hacia la temperatura ambiente.
        val rate = (1f - safeInsulation) * 0.04f
        val decayFactor = kotlin.math.exp(-rate * safeMinutes)
        val finalTemp = AMBIENT_TEMP + (safeInitial - AMBIENT_TEMP) * decayFactor

        val delta = safeInitial - finalTemp
        val summary = when {
            safeInsulation >= 0.7f -> "El buen aislamiento mantuvo la temperatura casi sin cambios."
            kotlin.math.abs(delta) < 2f -> "La temperatura casi no cambió en el tiempo medido."
            delta > 0 -> "El material se enfrió acercándose a la temperatura ambiente."
            else -> "El material se calentó acercándose a la temperatura ambiente."
        }

        return TemperatureResult(
            finalTemp = (finalTemp * 10).roundToInt() / 10f,
            summary = summary,
        )
    }
}
