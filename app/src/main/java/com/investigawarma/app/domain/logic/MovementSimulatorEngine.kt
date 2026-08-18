package com.investigawarma.app.domain.logic

import kotlin.math.roundToInt

/**
 * Motor real del simulador de movimiento. Aplica una fórmula física simplificada
 * (distancia proporcional a la fuerza aplicada e inversamente proporcional a la
 * fricción de la superficie y al peso del objeto) para que el resultado dependa
 * realmente de las variables elegidas, no de texto fijo.
 */
object MovementSimulatorEngine {

    /** superficie: 0 = hielo (baja fricción), 1 = madera, 2 = alfombra (alta fricción) */
    private val FRICTION_BY_SURFACE = mapOf(0 to 0.05f, 1 to 0.35f, 2 to 0.9f)
    private val SURFACE_NAMES = mapOf(0 to "hielo", 1 to "madera", 2 to "alfombra")

    data class MovementResult(
        val distanceMeters: Float,
        val summary: String,
    )

    fun simulate(surfaceIndex: Int, weightGrams: Float, forceNewtons: Float): MovementResult {
        val safeSurface = surfaceIndex.coerceIn(0, 2)
        val friction = FRICTION_BY_SURFACE.getValue(safeSurface)
        val safeWeight = weightGrams.coerceAtLeast(1f)
        val safeForce = forceNewtons.coerceAtLeast(0f)

        // distancia = (fuerza * 10) / (fricción * (peso / 100)), con tope razonable
        val rawDistance = (safeForce * 10f) / (friction * (safeWeight / 100f)).coerceAtLeast(0.01f)
        val distance = rawDistance.coerceIn(0f, 50f)

        val surfaceName = SURFACE_NAMES.getValue(safeSurface)
        val summary = when {
            safeForce <= 0f -> "Sin fuerza aplicada, el objeto no se movió."
            distance >= 30f -> "Con tan poca fricción en $surfaceName, el objeto se deslizó muy lejos."
            distance >= 10f -> "El objeto avanzó una distancia moderada sobre $surfaceName."
            else -> "La fricción de $surfaceName y el peso del objeto frenaron el movimiento rápidamente."
        }

        return MovementResult(
            distanceMeters = (distance * 100).roundToInt() / 100f,
            summary = summary,
        )
    }
}
