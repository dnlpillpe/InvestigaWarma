package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.investigawarma.app.data.local.entity.ExperimentEntity
import com.investigawarma.app.data.local.entity.ExperimentParameterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperimentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(experiments: List<ExperimentEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParameters(parameters: List<ExperimentParameterEntity>)

    @Transaction
    suspend fun insertWithParameters(
        experiments: List<ExperimentEntity>,
        parameters: List<ExperimentParameterEntity>,
    ) {
        insertAll(experiments)
        insertParameters(parameters)
    }

    @Query("SELECT * FROM experiment WHERE zone = :zone ORDER BY difficulty ASC")
    fun observeByZone(zone: String): Flow<List<ExperimentEntity>>

    @Query("SELECT * FROM experiment WHERE id = :experimentId LIMIT 1")
    suspend fun getById(experimentId: String): ExperimentEntity?

    @Query("SELECT * FROM experiment_parameter WHERE experimentId = :experimentId")
    suspend fun getParameters(experimentId: String): List<ExperimentParameterEntity>

    @Query("SELECT COUNT(*) FROM experiment")
    suspend fun count(): Int
}
