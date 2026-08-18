package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.investigawarma.app.data.local.entity.MissionStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionStepDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(steps: List<MissionStepEntity>)

    @Query("SELECT * FROM mission_step WHERE missionId = :missionId ORDER BY orderIndex ASC")
    fun observeByMission(missionId: String): Flow<List<MissionStepEntity>>

    @Query("SELECT COUNT(*) FROM mission_step")
    suspend fun count(): Int
}
