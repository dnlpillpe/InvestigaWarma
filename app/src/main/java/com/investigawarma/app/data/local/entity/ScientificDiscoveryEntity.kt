package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Un descubrimiento científico que el jugador obtiene al completar una misión. */
@Entity(
    tableName = "scientific_discovery",
    foreignKeys = [
        ForeignKey(
            entity = ScientificMissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["missionId"])],
)
data class ScientificDiscoveryEntity(
    @PrimaryKey val id: String,
    val missionId: String,
    val title: String,
    val description: String,
    val unlockedAt: Long? = null,
)
