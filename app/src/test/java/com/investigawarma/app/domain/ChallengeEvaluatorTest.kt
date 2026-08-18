package com.investigawarma.app.domain

import com.investigawarma.app.domain.logic.ChallengeEvaluator
import com.investigawarma.app.domain.model.BuildPayload
import com.investigawarma.app.domain.model.ClassifyItem
import com.investigawarma.app.domain.model.OrderPayload
import com.investigawarma.app.domain.model.PatternPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChallengeEvaluatorTest {

    @Test
    fun `detective correct index is success`() {
        assertTrue(ChallengeEvaluator.evaluateDetective(errorIndex = 2, chosenIndex = 2))
    }

    @Test
    fun `detective wrong index is failure`() {
        assertFalse(ChallengeEvaluator.evaluateDetective(errorIndex = 2, chosenIndex = 0))
    }

    @Test
    fun `order matching canonical sequence is success`() {
        val payload = OrderPayload(steps = listOf("observación", "pregunta", "hipótesis", "experimento", "conclusión"))
        assertTrue(ChallengeEvaluator.evaluateOrder(payload, payload.steps))
    }

    @Test
    fun `order with wrong sequence is failure`() {
        val payload = OrderPayload(steps = listOf("observación", "pregunta", "hipótesis", "experimento", "conclusión"))
        val wrong = listOf("conclusión", "pregunta", "hipótesis", "experimento", "observación")
        assertFalse(ChallengeEvaluator.evaluateOrder(payload, wrong))
    }

    @Test
    fun `order with mismatched size is failure (edge case)`() {
        val payload = OrderPayload(steps = listOf("a", "b", "c"))
        assertFalse(ChallengeEvaluator.evaluateOrder(payload, listOf("a", "b")))
    }

    @Test
    fun `pattern correct answer is success`() {
        val payload = PatternPayload(sequence = listOf(2, 4, 6, 8), answer = 10)
        assertTrue(ChallengeEvaluator.evaluatePattern(payload, 10))
    }

    @Test
    fun `pattern wrong answer is failure`() {
        val payload = PatternPayload(sequence = listOf(2, 4, 6, 8), answer = 10)
        assertFalse(ChallengeEvaluator.evaluatePattern(payload, 99))
    }

    @Test
    fun `classify all correct gives full accuracy and success`() {
        val items = listOf(ClassifyItem("perro", "vivo"), ClassifyItem("piedra", "no vivo"))
        val answers = mapOf("perro" to "vivo", "piedra" to "no vivo")
        val outcome = ChallengeEvaluator.evaluateClassify(items, answers)
        assertEquals(1f, outcome.accuracy, 0.001f)
        assertTrue(outcome.success)
    }

    @Test
    fun `classify below eighty percent is not success`() {
        val items = listOf(
            ClassifyItem("a", "x"), ClassifyItem("b", "x"),
            ClassifyItem("c", "y"), ClassifyItem("d", "y"), ClassifyItem("e", "y"),
        )
        val answers = mapOf("a" to "x", "b" to "y", "c" to "y", "d" to "x", "e" to "y")
        val outcome = ChallengeEvaluator.evaluateClassify(items, answers)
        assertTrue(outcome.accuracy < 0.8f)
        assertFalse(outcome.success)
    }

    @Test
    fun `classify with empty items is a defined non-crashing edge case`() {
        val outcome = ChallengeEvaluator.evaluateClassify(emptyList(), emptyMap())
        assertEquals(0f, outcome.accuracy, 0.001f)
        assertFalse(outcome.success)
    }

    @Test
    fun `build exact instrument match is success`() {
        val payload = BuildPayload(correctInstruments = listOf("lupa", "cuaderno"), distractorInstruments = listOf("balanza"))
        assertTrue(ChallengeEvaluator.evaluateBuild(payload, setOf("lupa", "cuaderno")))
    }

    @Test
    fun `build with an extra distractor selected is failure (edge case)`() {
        val payload = BuildPayload(correctInstruments = listOf("lupa", "cuaderno"), distractorInstruments = listOf("balanza"))
        assertFalse(ChallengeEvaluator.evaluateBuild(payload, setOf("lupa", "cuaderno", "balanza")))
    }

    @Test
    fun `build with a missing instrument is failure (edge case)`() {
        val payload = BuildPayload(correctInstruments = listOf("lupa", "cuaderno"), distractorInstruments = listOf("balanza"))
        assertFalse(ChallengeEvaluator.evaluateBuild(payload, setOf("lupa")))
    }

    @Test
    fun `build with nothing selected is failure (edge case)`() {
        val payload = BuildPayload(correctInstruments = listOf("lupa", "cuaderno"), distractorInstruments = listOf("balanza"))
        assertFalse(ChallengeEvaluator.evaluateBuild(payload, emptySet()))
    }
}
