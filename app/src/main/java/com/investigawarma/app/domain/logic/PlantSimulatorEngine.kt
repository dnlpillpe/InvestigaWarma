package com.investigawarma.app.domain.logic

import kotlin.math.roundToInt

/**
 * Motor real del simulador de plantas. Calcula el crecimiento a partir de
 * luz, agua y días transcurridos usando una curva con óptimos, no un texto
 * aleatorio: valores demasiado bajos o demasiado altos de cualquier variable
 * penalizan el crecimiento, igual que en una planta real.
 */
object PlantSimulatorEngine {

    data class PlantResult(
        val growthCm: Float,
        val healthPercent: Int,
        val summary: String,
    )

    private const val OPTIMAL_LIGHT = 60f
    private const val OPTIMAL_WATER = 55f

    fun simulate(light: Float, water: Float, days: Float): PlantResult {
        val safeLight = light.coerceIn(0f, 100f)
        val safeWater = water.coerceIn(0f, 100f)
        val safeDays = days.coerceIn(0f, 60f)

        // Penalización cuadrática por desviarse del óptimo (0..1, 1 = óptimo)
        val lightFactor = 1f - (kotlin.math.abs(safeLight - OPTIMAL_LIGHT) / 100f)
        val waterFactor = 1f - (kotlin.math.abs(safeWater - OPTIMAL_WATER) / 100f)

        val healthFactor = (lightFactor.coerceIn(0f, 1f) + waterFactor.coerceIn(0f, 1f)) / 2f
        val growth = safeDays * 0.35f * healthFactor
        val health = (healthFactor * 100).roundToInt().coerceIn(0, 100)

        val summary = when {
            safeWater < 15f -> "La planta casi no recibió agua: creció muy poco y se ve marchita."
            safeWater > 90f -> "Demasiada agua ahogó las raíces: el crecimiento se frenó."
            safeLight < 15f -> "Con tan poca luz la planta no pudo hacer fotosíntesis correctamente."
            safeLight > 95f -> "El exceso de luz estresó a la planta."
            health >= 80 -> "¡Condiciones casi perfectas! La planta creció fuerte y sana."
            health >= 50 -> "La planta creció de forma moderada. Aún se puede mejorar el equilibrio."
            else -> "La planta sobrevivió, pero el desequilibrio de luz y agua limitó su crecimiento."
        }

        return PlantResult(
            growthCm = (growth * 10).roundToInt() / 10f,
            healthPercent = health,
            summary = summary,
        )
    }
}
