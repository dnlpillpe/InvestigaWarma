package com.investigawarma.app.domain.model

import kotlinx.serialization.Serializable

/** Estructuras tipadas del campo ChallengeEntity.dataJson, una por ChallengeType. */

@Serializable
data class DetectivePayload(
    val statements: List<String>,
    val errorIndex: Int,
)

@Serializable
data class OrderPayload(
    val steps: List<String>,
)

@Serializable
data class PatternPayload(
    val sequence: List<Int>,
    val answer: Int,
)

@Serializable
data class ClassifyItem(
    val label: String,
    val category: String,
)

@Serializable
data class ClassifyPayload(
    val items: List<ClassifyItem>,
    val categories: List<String>,
)

@Serializable
data class BuildPayload(
    val correctInstruments: List<String>,
    val distractorInstruments: List<String>,
)
