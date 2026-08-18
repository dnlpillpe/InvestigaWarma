package com.investigawarma.app.domain.logic

import com.investigawarma.app.domain.model.BuildPayload
import com.investigawarma.app.domain.model.ClassifyItem
import com.investigawarma.app.domain.model.OrderPayload
import com.investigawarma.app.domain.model.PatternPayload

/**
 * Evalúa las respuestas del jugador para cada tipo de desafío. Lógica pura,
 * sin dependencias de Android: fácil de probar exhaustivamente.
 */
object ChallengeEvaluator {

    /** DETECTIVE: el jugador elige el índice del enunciado que cree erróneo. */
    fun evaluateDetective(errorIndex: Int, chosenIndex: Int): Boolean = errorIndex == chosenIndex

    /** ORDER: el jugador propone un orden; se compara contra el orden canónico. */
    fun evaluateOrder(payload: OrderPayload, proposedOrder: List<String>): Boolean {
        if (proposedOrder.size != payload.steps.size) return false
        return proposedOrder == payload.steps
    }

    /** PATTERN: el jugador predice el siguiente valor de una secuencia. */
    fun evaluatePattern(payload: PatternPayload, proposedAnswer: Int): Boolean =
        proposedAnswer == payload.answer

    /**
     * CLASSIFY: el jugador asigna una categoría a cada item.
     * Devuelve el porcentaje de aciertos (0..1) y si el intento se considera exitoso (>=80%).
     */
    data class ClassifyOutcome(val accuracy: Float, val success: Boolean)

    fun evaluateClassify(items: List<ClassifyItem>, answers: Map<String, String>): ClassifyOutcome {
        if (items.isEmpty()) return ClassifyOutcome(0f, false)
        val correct = items.count { item -> answers[item.label] == item.category }
        val accuracy = correct.toFloat() / items.size
        return ClassifyOutcome(accuracy, accuracy >= 0.8f)
    }

    /**
     * BUILD: el jugador selecciona instrumentos. Éxito si eligió exactamente el
     * conjunto correcto (ni de más, ni de menos).
     */
    fun evaluateBuild(payload: BuildPayload, selected: Set<String>): Boolean {
        val correctSet = payload.correctInstruments.toSet()
        return selected == correctSet
    }
}
