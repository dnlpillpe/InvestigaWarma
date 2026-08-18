package com.investigawarma.app.domain

import com.investigawarma.app.domain.logic.HypothesisValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HypothesisValidatorTest {

    @Test
    fun `well formed hypothesis is valid`() {
        val result = HypothesisValidator.validate(
            variableText = "la cantidad de luz",
            resultText = "la planta crecerá más rápido",
            explanationText = "la luz ayuda a la fotosíntesis",
        )
        assertTrue(result.isValid)
    }

    @Test
    fun `empty variable is invalid (edge case)`() {
        val result = HypothesisValidator.validate("", "resultado válido", "explicación válida")
        assertFalse(result.isValid)
    }

    @Test
    fun `empty result is invalid (edge case)`() {
        val result = HypothesisValidator.validate("variable válida", "   ", "explicación válida")
        assertFalse(result.isValid)
    }

    @Test
    fun `empty explanation is invalid (edge case)`() {
        val result = HypothesisValidator.validate("variable válida", "resultado válido", "")
        assertFalse(result.isValid)
    }

    @Test
    fun `too short field is invalid`() {
        val result = HypothesisValidator.validate("ok", "resultado válido", "explicación válida")
        assertFalse(result.isValid)
    }

    @Test
    fun `excessively long field is invalid (edge case)`() {
        val longText = "a".repeat(300)
        val result = HypothesisValidator.validate(longText, "resultado válido", "explicación válida")
        assertFalse(result.isValid)
    }

    @Test
    fun `identical variable and result is invalid`() {
        val result = HypothesisValidator.validate("La luz solar", "la luz solar", "porque sí")
        assertFalse(result.isValid)
    }

    @Test
    fun `different content with different casing is valid`() {
        val result = HypothesisValidator.validate("la cantidad de agua", "LA PLANTA CRECE MENOS", "el exceso ahoga las raíces")
        assertTrue(result.isValid)
    }

    @Test
    fun `whitespace-only fields are treated as empty (edge case)`() {
        val result = HypothesisValidator.validate("   ", "   ", "   ")
        assertFalse(result.isValid)
    }
}
