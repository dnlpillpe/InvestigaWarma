package com.investigawarma.app.domain.logic

/**
 * Valida que una hipótesis construida por el jugador (formato SI / ENTONCES / PORQUE)
 * tenga una estructura mínimamente completa. No juzga si el contenido científico es
 * "correcto" (eso requeriría NLP), pero sí evita hipótesis vacías, copiadas del
 * placeholder o demasiado cortas para ser una explicación real.
 */
object HypothesisValidator {

    private const val MIN_LENGTH = 3
    private const val MAX_LENGTH = 280

    data class ValidationResult(
        val isValid: Boolean,
        val reason: String? = null,
    )

    fun validate(variableText: String, resultText: String, explanationText: String): ValidationResult {
        val fields = listOf("variable" to variableText, "resultado" to resultText, "explicación" to explanationText)

        for ((name, value) in fields) {
            val trimmed = value.trim()
            if (trimmed.isEmpty()) {
                return ValidationResult(false, "El campo $name está vacío.")
            }
            if (trimmed.length < MIN_LENGTH) {
                return ValidationResult(false, "El campo $name es demasiado corto.")
            }
            if (trimmed.length > MAX_LENGTH) {
                return ValidationResult(false, "El campo $name es demasiado largo.")
            }
        }

        if (variableText.trim().equals(resultText.trim(), ignoreCase = true)) {
            return ValidationResult(false, "La variable y el resultado no pueden ser idénticos.")
        }

        return ValidationResult(true)
    }
}
