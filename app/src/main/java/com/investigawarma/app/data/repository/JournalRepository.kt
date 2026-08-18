package com.investigawarma.app.data.repository

import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.data.local.entity.ScientificJournalEntity
import com.investigawarma.app.data.local.entity.VoiceEntryEntity
import com.investigawarma.app.domain.model.JournalEntryType
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val db: AppDatabase) {

    fun observeEntries(): Flow<List<ScientificJournalEntity>> = db.scientificJournalDao().observeAll()

    fun observeVoiceEntries(journalId: Long) = db.voiceEntryDao().observeByJournal(journalId)

    suspend fun addTextEntry(title: String, content: String, missionId: String?): Long {
        val safeTitle = title.trim().ifBlank { "Nota sin título" }.take(60)
        val safeContent = content.trim().take(2000)
        return db.scientificJournalDao().insert(
            ScientificJournalEntity(
                missionId = missionId,
                title = safeTitle,
                content = safeContent,
                type = JournalEntryType.TEXT.name,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    /** Crea la entrada de diario asociada a una grabación de voz (máx. 60s, ya validado por el grabador). */
    suspend fun addVoiceEntry(title: String, missionId: String?, filePath: String, durationSeconds: Int): Long {
        val journalId = db.scientificJournalDao().insert(
            ScientificJournalEntity(
                missionId = missionId,
                title = title.trim().ifBlank { "Nota de voz" }.take(60),
                content = "",
                type = JournalEntryType.VOICE.name,
                createdAt = System.currentTimeMillis(),
            ),
        )
        db.voiceEntryDao().insert(
            VoiceEntryEntity(
                journalId = journalId,
                filePath = filePath,
                durationSeconds = durationSeconds.coerceIn(0, 60),
                createdAt = System.currentTimeMillis(),
            ),
        )
        return journalId
    }

    suspend fun getVoiceEntry(journalId: Long) = db.voiceEntryDao().getByJournal(journalId)

    suspend fun deleteEntry(entry: ScientificJournalEntity) = db.scientificJournalDao().delete(entry)

    suspend fun deleteVoiceEntry(entry: VoiceEntryEntity) = db.voiceEntryDao().delete(entry)

    suspend fun entryCount(): Int = db.scientificJournalDao().count()
}
