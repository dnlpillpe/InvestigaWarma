package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.investigawarma.app.data.local.entity.ScientificMissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScientificMissionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(missions: List<ScientificMissionEntity>)

    @Query("SELECT * FROM scientific_mission WHERE zone = :zone ORDER BY orderIndex ASC")
    fun observeByZone(zone: String): Flow<List<ScientificMissionEntity>>

    @Query("SELECT * FROM scientific_mission WHERE id = :missionId LIMIT 1")
    suspend fun getById(missionId: String): ScientificMissionEntity?

    @Query("SELECT * FROM scientific_mission WHERE id = :missionId LIMIT 1")
    fun observeById(missionId: String): Flow<ScientificMissionEntity?>

    @Query("SELECT COUNT(*) FROM scientific_mission")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM scientific_mission WHERE zone = :zone")
    suspend fun countByZone(zone: String): Int

    @Query("SELECT * FROM scientific_mission ORDER BY zone, orderIndex")
    fun observeAll(): Flow<List<ScientificMissionEntity>>
}
