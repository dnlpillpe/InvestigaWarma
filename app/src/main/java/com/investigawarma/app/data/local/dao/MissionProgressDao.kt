package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.investigawarma.app.data.local.entity.MissionProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(progress: List<MissionProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: MissionProgressEntity)

    @Update
    suspend fun update(progress: MissionProgressEntity)

    @Query("SELECT * FROM mission_progress WHERE missionId = :missionId LIMIT 1")
    suspend fun getByMission(missionId: String): MissionProgressEntity?

    @Query("SELECT * FROM mission_progress WHERE missionId = :missionId LIMIT 1")
    fun observeByMission(missionId: String): Flow<MissionProgressEntity?>

    @Query("SELECT * FROM mission_progress")
    fun observeAll(): Flow<List<MissionProgressEntity>>

    @Query("SELECT COUNT(*) FROM mission_progress WHERE status = 'COMPLETED' OR status = 'MASTERED'")
    suspend fun countCompleted(): Int

    @Query(
        "SELECT mp.* FROM mission_progress mp " +
            "INNER JOIN scientific_mission sm ON sm.id = mp.missionId " +
            "WHERE sm.zone = :zone",
    )
    fun observeByZone(zone: String): Flow<List<MissionProgressEntity>>
}
