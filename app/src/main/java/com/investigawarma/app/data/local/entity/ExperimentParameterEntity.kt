package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Una variable manipulable de un experimento (ej: luz, agua, peso, superficie). */
@Entity(
    tableName = "experiment_parameter",
    foreignKeys = [
        ForeignKey(
            entity = ExperimentEntity::class,
            parentColumns = ["id"],
            childColumns = ["experimentId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["experimentId"])],
)
data class ExperimentParameterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val experimentId: String,
    val name: String,
    val unit: String,
    val minValue: Float,
    val maxValue: Float,
    val stepValue: Float,
    val defaultValue: Float,
)
