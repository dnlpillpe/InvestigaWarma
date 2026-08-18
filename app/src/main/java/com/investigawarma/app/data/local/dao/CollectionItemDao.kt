package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.investigawarma.app.data.local.entity.CollectionItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<CollectionItemEntity>)

    @Query("SELECT * FROM collection_item ORDER BY category, name")
    fun observeAll(): Flow<List<CollectionItemEntity>>

    @Query("SELECT * FROM collection_item WHERE unlockedAt IS NOT NULL ORDER BY unlockedAt DESC")
    fun observeUnlocked(): Flow<List<CollectionItemEntity>>

    @Query("UPDATE collection_item SET unlockedAt = :unlockedAt WHERE key = :key AND unlockedAt IS NULL")
    suspend fun unlockByKey(key: String, unlockedAt: Long)

    @Query("SELECT COUNT(*) FROM collection_item WHERE unlockedAt IS NOT NULL")
    suspend fun countUnlocked(): Int

    @Query("SELECT COUNT(*) FROM collection_item")
    suspend fun countTotal(): Int
}
