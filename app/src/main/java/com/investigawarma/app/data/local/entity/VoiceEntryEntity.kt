package com.investigawarma.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Grabación de audio del "diario científico hablado". El archivo se guarda en el
 * almacenamiento privado de la app (filesDir/voice_journal), máximo 60 segundos.
 * No se usa reconocimiento de voz: solo grabar, reproducir y eliminar.
 */
@Entity(
    tableName = "voice_entry",
    foreignKeys = [
        ForeignKey(
            entity = ScientificJournalEntity::class,
            parentColumns = ["id"],
            childColumns = ["journalId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["journalId"])],
)
data class VoiceEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val journalId: Long,
    val filePath: String,
    val durationSeconds: Int,
    val createdAt: Long,
)
