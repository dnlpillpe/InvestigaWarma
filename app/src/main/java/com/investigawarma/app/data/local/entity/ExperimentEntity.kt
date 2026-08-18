package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Un experimento del Laboratorio Experimental. Puede estar ligado a una misión
 * (experimento guiado) o ser de exploración libre (missionId nulo).
 */
@Entity(
    tableName = "experiment",
    indices = [Index(value = ["zone"]), Index(value = ["missionId"])],
)
data class ExperimentEntity(
    @PrimaryKey val id: String,
    val zone: String,
    val title: String,
    val description: String,
    val simulatorType: String,
    val difficulty: Int,
    val missionId: String? = null,
)
