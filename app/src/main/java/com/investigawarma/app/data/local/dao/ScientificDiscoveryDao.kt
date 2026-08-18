package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.investigawarma.app.data.local.entity.ScientificDiscoveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScientificDiscoveryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(discoveries: List<ScientificDiscoveryEntity>)

    @Query("SELECT * FROM scientific_discovery WHERE unlockedAt IS NOT NULL ORDER BY unlockedAt DESC")
    fun observeUnlocked(): Flow<List<ScientificDiscoveryEntity>>

    @Query("SELECT * FROM scientific_discovery WHERE missionId = :missionId LIMIT 1")
    suspend fun getByMission(missionId: String): ScientificDiscoveryEntity?

    @Query("UPDATE scientific_discovery SET unlockedAt = :unlockedAt WHERE missionId = :missionId AND unlockedAt IS NULL")
    suspend fun unlockForMission(missionId: String, unlockedAt: Long)

    @Query("SELECT COUNT(*) FROM scientific_discovery WHERE unlockedAt IS NOT NULL")
    suspend fun countUnlocked(): Int
}
