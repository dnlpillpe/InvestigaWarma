package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.investigawarma.app.data.local.entity.ScientificJournalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScientificJournalDao {

    @Insert
    suspend fun insert(entry: ScientificJournalEntity): Long

    @Delete
    suspend fun delete(entry: ScientificJournalEntity)

    @Query("SELECT * FROM scientific_journal ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ScientificJournalEntity>>

    @Query("SELECT * FROM scientific_journal WHERE missionId = :missionId ORDER BY createdAt DESC")
    fun observeByMission(missionId: String): Flow<List<ScientificJournalEntity>>

    @Query("SELECT * FROM scientific_journal WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ScientificJournalEntity?

    @Query("SELECT COUNT(*) FROM scientific_journal")
    suspend fun count(): Int
}
