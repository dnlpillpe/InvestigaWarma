package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.investigawarma.app.data.local.entity.HypothesisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HypothesisDao {

    @Insert
    suspend fun insert(hypothesis: HypothesisEntity): Long

    @Query("SELECT * FROM hypothesis WHERE missionId = :missionId ORDER BY createdAt DESC")
    fun observeByMission(missionId: String): Flow<List<HypothesisEntity>>

    @Query("SELECT * FROM hypothesis ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<HypothesisEntity>>

    @Query("SELECT COUNT(*) FROM hypothesis WHERE isValidStructure = 1")
    suspend fun countValid(): Int

    @Query("SELECT COUNT(*) FROM hypothesis")
    suspend fun countTotal(): Int
}
