package com.investigawarma.app

import android.content.Context
import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.data.local.DatabaseSeeder
import com.investigawarma.app.data.repository.ChallengeRepository
import com.investigawarma.app.data.repository.CollectionRepository
import com.investigawarma.app.data.repository.ExperimentRepository
import com.investigawarma.app.data.repository.JournalRepository
import com.investigawarma.app.data.repository.MissionRepository
import com.investigawarma.app.data.repository.PlayerRepository
import com.investigawarma.app.data.repository.StatsRepository
import com.investigawarma.app.util.HapticsHelper
import com.investigawarma.app.util.SoundHelper
import com.investigawarma.app.util.VoiceRecorderManager

/**
 * Contenedor de dependencias manual (AppContainer), según lo recomendado por la
 * especificación maestra: prioriza simplicidad y estabilidad frente a frameworks
 * de inyección adicionales. Todas las instancias son singletons de proceso.
 */
class AppContainer(context: Context) {

    val database: AppDatabase = AppDatabase.getInstance(context)
    val seeder: DatabaseSeeder = DatabaseSeeder(database)

    val playerRepository: PlayerRepository = PlayerRepository(database)
    val missionRepository: MissionRepository = MissionRepository(database)
    val experimentRepository: ExperimentRepository = ExperimentRepository(database)
    val challengeRepository: ChallengeRepository = ChallengeRepository(database)
    val collectionRepository: CollectionRepository = CollectionRepository(database)
    val journalRepository: JournalRepository = JournalRepository(database)
    val statsRepository: StatsRepository = StatsRepository(database)

    val voiceRecorderManager: VoiceRecorderManager = VoiceRecorderManager(context.applicationContext)
    val soundHelper: SoundHelper = SoundHelper()
    val hapticsHelper: HapticsHelper = HapticsHelper(context.applicationContext)
}
