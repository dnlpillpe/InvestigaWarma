package com.investigawarma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.investigawarma.app.data.local.entity.ChallengeAttemptEntity
import com.investigawarma.app.data.local.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(challenges: List<ChallengeEntity>)

    @Insert
    suspend fun insertAttempt(attempt: ChallengeAttemptEntity): Long

    @Query("SELECT * FROM challenge WHERE zone = :zone ORDER BY difficulty ASC")
    fun observeByZone(zone: String): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenge WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ChallengeEntity?

    @Query("SELECT * FROM challenge_attempt WHERE challengeId = :challengeId ORDER BY createdAt DESC")
    fun observeAttempts(challengeId: String): Flow<List<ChallengeAttemptEntity>>

    @Query("SELECT * FROM challenge_attempt ORDER BY createdAt DESC")
    fun observeAllAttempts(): Flow<List<ChallengeAttemptEntity>>

    @Query("SELECT COUNT(*) FROM challenge_attempt WHERE success = 1")
    suspend fun countSuccessfulAttempts(): Int

    @Query("SELECT COUNT(*) FROM challenge_attempt")
    suspend fun countTotalAttempts(): Int

    @Query("SELECT COUNT(*) FROM challenge")
    suspend fun count(): Int

    @Query(
        "SELECT c.* FROM challenge c WHERE c.id NOT IN " +
            "(SELECT DISTINCT challengeId FROM challenge_attempt WHERE success = 1) " +
            "ORDER BY c.difficulty ASC LIMIT :limit",
    )
    suspend fun getPendingReview(limit: Int): List<ChallengeEntity>
}
