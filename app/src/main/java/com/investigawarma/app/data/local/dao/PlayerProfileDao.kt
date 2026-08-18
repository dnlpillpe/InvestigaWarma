package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.investigawarma.app.data.local.entity.PlayerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: PlayerProfileEntity)

    @Update
    suspend fun update(profile: PlayerProfileEntity)

    @Query("UPDATE player_profile SET xp = xp + :xpDelta, level = :newLevel WHERE id = 1")
    suspend fun addXp(xpDelta: Int, newLevel: Int)

    @Query("UPDATE player_profile SET soundEnabled = :enabled WHERE id = 1")
    suspend fun setSoundEnabled(enabled: Boolean)

    @Query("UPDATE player_profile SET hapticsEnabled = :enabled WHERE id = 1")
    suspend fun setHapticsEnabled(enabled: Boolean)

    @Query("UPDATE player_profile SET onboardingCompleted = 1 WHERE id = 1")
    suspend fun markOnboardingCompleted()

    @Query("DELETE FROM player_profile")
    suspend fun clear()
}
