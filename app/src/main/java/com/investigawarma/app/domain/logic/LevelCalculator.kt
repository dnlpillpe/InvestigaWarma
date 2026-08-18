package com.investigawarma.app.domain.logic

import com.investigawarma.app.domain.model.PlayerLevel
import kotlin.math.roundToInt

/**
 * Lógica pura de progresión de nivel a partir de la experiencia (XP) acumulada.
 * No depende de Android ni de Room: se puede probar en JVM puro.
 */
object LevelCalculator {

    /** Nunca negativo: un XP negativo (dato corrupto) se trata como 0. */
    fun levelFor(xp: Int): PlayerLevel = PlayerLevel.forXp(xp.coerceAtLeast(0))

    fun levelIndex(level: PlayerLevel): Int =
        PlayerLevel.entries.sortedBy { it.minXp }.indexOf(level) + 1

    /**
     * Porcentaje de avance (0..100) hacia el siguiente nivel.
     * Si ya está en el nivel máximo, devuelve 100.
     */
    fun progressToNextLevel(xp: Int): Int {
        val safeXp = xp.coerceAtLeast(0)
        val current = levelFor(safeXp)
        val next = PlayerLevel.next(current) ?: return 100
        val span = (next.minXp - current.minXp).coerceAtLeast(1)
        val advanced = (safeXp - current.minXp).coerceIn(0, span)
        return ((advanced.toDouble() / span) * 100).roundToInt().coerceIn(0, 100)
    }

    fun xpToNextLevel(xp: Int): Int {
        val safeXp = xp.coerceAtLeast(0)
        val current = levelFor(safeXp)
        val next = PlayerLevel.next(current) ?: return 0
        return (next.minXp - safeXp).coerceAtLeast(0)
    }
}
