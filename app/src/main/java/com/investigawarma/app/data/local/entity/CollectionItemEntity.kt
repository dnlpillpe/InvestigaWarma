package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Un objeto del Museo Científico Personal. Se desbloquea realmente mediante
 * progreso (no se muestra "bloqueado" como simple imagen sin lógica detrás).
 */
@Entity(
    tableName = "collection_item",
    indices = [Index(value = ["key"], unique = true), Index(value = ["category"])],
)
data class CollectionItemEntity(
    @PrimaryKey val id: String,
    val key: String,
    val name: String,
    val description: String,
    val category: String,
    val requirementDescription: String,
    val unlockedAt: Long? = null,
    /** Clave de IllustrationKey (ver SceneIllustrations.kt) para mostrar una imagen real en el Museo. */
    val illustrationKey: String? = null,
)
