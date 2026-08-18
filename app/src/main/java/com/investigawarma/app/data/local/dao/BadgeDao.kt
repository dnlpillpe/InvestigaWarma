package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.investigawarma.app.data.local.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<BadgeEntity>)

    @Query("SELECT * FROM badge ORDER BY category, name")
    fun observeAll(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badge WHERE unlockedAt IS NOT NULL ORDER BY unlockedAt DESC")
    fun observeUnlocked(): Flow<List<BadgeEntity>>

    @Query("UPDATE badge SET unlockedAt = :unlockedAt WHERE key = :key AND unlockedAt IS NULL")
    suspend fun unlockByKey(key: String, unlockedAt: Long)

    @Query("SELECT unlockedAt IS NOT NULL FROM badge WHERE key = :key LIMIT 1")
    suspend fun isUnlocked(key: String): Boolean?

    @Query("SELECT COUNT(*) FROM badge WHERE unlockedAt IS NOT NULL")
    suspend fun countUnlocked(): Int

    @Query("SELECT COUNT(*) FROM badge")
    suspend fun countTotal(): Int
}
