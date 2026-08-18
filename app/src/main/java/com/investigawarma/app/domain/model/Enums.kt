package com.investigawarma.app.domain.model

/** Las seis zonas de la Academia Científica (ver mapa principal). */
enum class Zone(val displayOrder: Int) {
    SALA_OBSERVACION(1),
    LABORATORIO_EXPERIMENTAL(2),
    BIODESCUBRIMIENTO(3),
    PLANETA_TIERRA(4),
    CENTRO_DE_DATOS(5),
    MUSEO_CIENTIFICO(6),
}

/** Mecánica central de una misión. Nunca únicamente opción múltiple. */
enum class MissionMechanicType {
    OBSERVE_COMPARE,
    CLASSIFY,
    ORDER_STEPS,
    DRAG_DROP_LABEL,
    BUILD_QUESTION,
    BUILD_HYPOTHESIS,
    SIMULATE_PLANT,
    SIMULATE_MOVEMENT,
    SIMULATE_TEMPERATURE,
    DETECTIVE_FIND_ERROR,
    FIND_PATTERN,
    PREDICT_OUTCOME,
    MULTIPLE_CHOICE,
}

enum class MissionStatus {
    LOCKED,
    AVAILABLE,
    STARTED,
    COMPLETED,
    MASTERED,
}

enum class MissionStepType {
    OBSERVE,
    QUESTION,
    HYPOTHESIS,
    EXPERIMENT,
    ANALYZE,
    DISCOVERY,
}

enum class SimulatorType {
    PLANT,
    MOVEMENT,
    TEMPERATURE,
    NONE,
}

enum class CollectionCategory {
    INICIAL,
    INTERMEDIO,
    AVANZADO,
}

enum class JournalEntryType {
    TEXT,
    VOICE,
}

enum class ChallengeType {
    DETECTIVE,
    ORDER,
    PATTERN,
    CLASSIFY,
    BUILD,
}

enum class PlayerLevel(val minXp: Int, val displayName: String) {
    EXPLORADOR_CIENTIFICO(0, "Explorador Científico"),
    INVESTIGADOR_JUNIOR(300, "Investigador Junior"),
    CIENTIFICO_EN_FORMACION(800, "Científico en Formación"),
    GRAN_DESCUBRIDOR(1600, "Gran Descubridor");

    companion object {
        fun forXp(xp: Int): PlayerLevel =
            entries.sortedByDescending { it.minXp }.first { xp >= it.minXp }

        fun next(current: PlayerLevel): PlayerLevel? {
            val ordered = entries.sortedBy { it.minXp }
            val idx = ordered.indexOf(current)
            return ordered.getOrNull(idx + 1)
        }
    }
}
