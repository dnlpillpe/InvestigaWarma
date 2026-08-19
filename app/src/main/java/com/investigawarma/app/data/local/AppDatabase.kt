package com.investigawarma.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.investigawarma.app.data.local.converters.Converters
import com.investigawarma.app.data.local.dao.BadgeDao
import com.investigawarma.app.data.local.dao.ChallengeDao
import com.investigawarma.app.data.local.dao.CollectionItemDao
import com.investigawarma.app.data.local.dao.ExperimentDao
import com.investigawarma.app.data.local.dao.ExperimentResultDao
import com.investigawarma.app.data.local.dao.HypothesisDao
import com.investigawarma.app.data.local.dao.MissionProgressDao
import com.investigawarma.app.data.local.dao.MissionStepDao
import com.investigawarma.app.data.local.dao.PlayerProfileDao
import com.investigawarma.app.data.local.dao.ScientificDiscoveryDao
import com.investigawarma.app.data.local.dao.ScientificJournalDao
import com.investigawarma.app.data.local.dao.ScientificMissionDao
import com.investigawarma.app.data.local.dao.VoiceEntryDao
import com.investigawarma.app.data.local.entity.BadgeEntity
import com.investigawarma.app.data.local.entity.ChallengeAttemptEntity
import com.investigawarma.app.data.local.entity.ChallengeEntity
import com.investigawarma.app.data.local.entity.CollectionItemEntity
import com.investigawarma.app.data.local.entity.ExperimentEntity
import com.investigawarma.app.data.local.entity.ExperimentParameterEntity
import com.investigawarma.app.data.local.entity.ExperimentResultEntity
import com.investigawarma.app.data.local.entity.HypothesisEntity
import com.investigawarma.app.data.local.entity.MissionProgressEntity
import com.investigawarma.app.data.local.entity.MissionStepEntity
import com.investigawarma.app.data.local.entity.PlayerProfileEntity
import com.investigawarma.app.data.local.entity.ScientificDiscoveryEntity
import com.investigawarma.app.data.local.entity.ScientificJournalEntity
import com.investigawarma.app.data.local.entity.ScientificMissionEntity
import com.investigawarma.app.data.local.entity.VoiceEntryEntity

/**
 * Base de datos Room de InvestigaWarma. Toda la persistencia del jugador vive
 * aquí; no hay backend ni sincronización remota.
 *
 * version = 2: se agregó CollectionItemEntity.illustrationKey. No existen
 * usuarios con datos reales todavía (sin Migration previas en el repo), así
 * que se usa fallbackToDestructiveMigration() en vez de escribir una
 * migración manual de una sola columna.
 */
@Database(
    entities = [
        PlayerProfileEntity::class,
        ScientificMissionEntity::class,
        MissionStepEntity::class,
        HypothesisEntity::class,
        ExperimentEntity::class,
        ExperimentParameterEntity::class,
        ExperimentResultEntity::class,
        ScientificDiscoveryEntity::class,
        CollectionItemEntity::class,
        BadgeEntity::class,
        ScientificJournalEntity::class,
        VoiceEntryEntity::class,
        MissionProgressEntity::class,
        ChallengeEntity::class,
        ChallengeAttemptEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerProfileDao(): PlayerProfileDao
    abstract fun scientificMissionDao(): ScientificMissionDao
    abstract fun missionStepDao(): MissionStepDao
    abstract fun hypothesisDao(): HypothesisDao
    abstract fun experimentDao(): ExperimentDao
    abstract fun experimentResultDao(): ExperimentResultDao
    abstract fun scientificDiscoveryDao(): ScientificDiscoveryDao
    abstract fun collectionItemDao(): CollectionItemDao
    abstract fun badgeDao(): BadgeDao
    abstract fun scientificJournalDao(): ScientificJournalDao
    abstract fun voiceEntryDao(): VoiceEntryDao
    abstract fun missionProgressDao(): MissionProgressDao
    abstract fun challengeDao(): ChallengeDao

    companion object {
        const val DATABASE_NAME = "investigawarma.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
