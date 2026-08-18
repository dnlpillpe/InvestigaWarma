package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Entrada del cuaderno científico personal del jugador (texto o voz). */
@Entity(
    tableName = "scientific_journal",
    foreignKeys = [
        ForeignKey(
            entity = ScientificMissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index(value = ["missionId"]), Index(value = ["createdAt"])],
)
data class ScientificJournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val missionId: String? = null,
    val title: String,
    val content: String,
    val type: String,
    val createdAt: Long,
)
