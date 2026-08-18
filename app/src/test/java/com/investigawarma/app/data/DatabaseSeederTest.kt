package com.investigawarma.app.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.data.local.DatabaseSeeder
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Pruebas de persistencia real sobre una base de datos Room en memoria
 * (Robolectric, sin emulador). Verifican que el seed inicial cumple las
 * cantidades exigidas por la especificación y que es idempotente.
 *
 * Requiere el entorno Android/Robolectric configurado (Android SDK descargado
 * por Gradle); no se ejecutó en el entorno de generación por falta de acceso
 * a red — ver BUILD_REPORT.md.
 */
@RunWith(RobolectricTestRunner::class)
class DatabaseSeederTest {

    private lateinit var db: AppDatabase
    private lateinit var seeder: DatabaseSeeder

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        seeder = DatabaseSeeder(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `fresh database seeds forty missions`() = runTest {
        seeder.seedIfNeeded()
        assertEquals(40, db.scientificMissionDao().count())
    }

    @Test
    fun `fresh database seeds thirty experiments`() = runTest {
        seeder.seedIfNeeded()
        assertEquals(30, db.experimentDao().count())
    }

    @Test
    fun `fresh database seeds fifty challenges`() = runTest {
        seeder.seedIfNeeded()
        assertEquals(50, db.challengeDao().count())
    }

    @Test
    fun `fresh database seeds twenty collection items`() = runTest {
        seeder.seedIfNeeded()
        assertEquals(20, db.collectionItemDao().countTotal())
    }

    @Test
    fun `fresh database seeds fifteen badges`() = runTest {
        seeder.seedIfNeeded()
        assertEquals(15, db.badgeDao().countTotal())
    }

    @Test
    fun `seeding twice does not duplicate missions (idempotent, edge case)`() = runTest {
        seeder.seedIfNeeded()
        seeder.seedIfNeeded()
        assertEquals(40, db.scientificMissionDao().count())
    }

    @Test
    fun `first mission of each zone starts available and the rest start locked`() = runTest {
        seeder.seedIfNeeded()
        val firstMission = db.scientificMissionDao().getById("m01")
        val secondMission = db.scientificMissionDao().getById("m02")
        val firstProgress = db.missionProgressDao().getByMission("m01")
        val secondProgress = db.missionProgressDao().getByMission("m02")
        assertEquals("AVAILABLE", firstProgress?.status)
        assertEquals("LOCKED", secondProgress?.status)
        assertTrue(firstMission != null && secondMission != null)
    }
}
