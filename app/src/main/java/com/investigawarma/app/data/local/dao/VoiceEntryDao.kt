package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.investigawarma.app.data.local.entity.VoiceEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceEntryDao {

    @Insert
    suspend fun insert(entry: VoiceEntryEntity): Long

    @Delete
    suspend fun delete(entry: VoiceEntryEntity)

    @Query("SELECT * FROM voice_entry WHERE journalId = :journalId ORDER BY createdAt DESC")
    fun observeByJournal(journalId: Long): Flow<List<VoiceEntryEntity>>

    @Query("SELECT * FROM voice_entry WHERE journalId = :journalId LIMIT 1")
    suspend fun getByJournal(journalId: Long): VoiceEntryEntity?

    @Query("SELECT * FROM voice_entry ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<VoiceEntryEntity>>
}
