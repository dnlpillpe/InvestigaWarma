package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.investigawarma.app.data.local.entity.ExperimentResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperimentResultDao {

    @Insert
    suspend fun insert(result: ExperimentResultEntity): Long

    @Query("SELECT * FROM experiment_result WHERE experimentId = :experimentId ORDER BY createdAt DESC")
    fun observeByExperiment(experimentId: String): Flow<List<ExperimentResultEntity>>

    @Query("SELECT * FROM experiment_result ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ExperimentResultEntity>>

    @Query("SELECT AVG(outcomeScore) FROM experiment_result WHERE experimentId = :experimentId")
    suspend fun averageScore(experimentId: String): Float?

    @Query("SELECT COUNT(*) FROM experiment_result")
    suspend fun count(): Int
}
