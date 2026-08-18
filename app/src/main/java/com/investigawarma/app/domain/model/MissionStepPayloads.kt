package com.investigawarma.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Payload del paso EXPERIMENT de una misión (MissionStepEntity.contentJson).
 * "kind" determina qué campos son relevantes:
 *  - "experiment": experimentId (simuladores planta/movimiento/temperatura)
 *  - "challenge": challengeId (reutiliza un desafío de clasificar/ordenar/patrón/detective/construir)
 *  - "compare": prompt + statements + correctIndices (comparar observaciones)
 *  - "drag_drop": prompt + pairs (arrastrar etiqueta a definición)
 *  - "predict": scenario + options + correctIndex (predecir resultado)
 *  - "generic": instructions (mecánicas sin interacción estructurada adicional)
 */
@Serializable
data class MissionExperimentContent(
    val kind: String,
    val experimentId: String? = null,
    val challengeId: String? = null,
    val prompt: String? = null,
    val statements: List<String>? = null,
    val correctIndices: List<Int>? = null,
    val pairs: List<DragDropPair>? = null,
    val scenario: String? = null,
    val options: List<String>? = null,
    val correctIndex: Int? = null,
    val instructions: String? = null,
)

@Serializable
data class DragDropPair(val label: String, val definition: String)

@Serializable
data class QuestionStepContent(
    val connectors: List<String> = emptyList(),
    val topic: String = "",
)

@Serializable
data class HypothesisStepContent(
    val variableHint: String = "",
    val resultHint: String = "",
    val explanationHint: String = "",
)
