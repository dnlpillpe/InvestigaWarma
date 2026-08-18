package com.investigawarma.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.investigawarma.app.AppContainer
import com.investigawarma.app.domain.model.Zone
import com.investigawarma.app.ui.screens.HomeScreen
import com.investigawarma.app.ui.screens.JournalScreen
import com.investigawarma.app.ui.screens.MissionScreen
import com.investigawarma.app.ui.screens.MuseumScreen
import com.investigawarma.app.ui.screens.OnboardingScreen
import com.investigawarma.app.ui.screens.SettingsScreen
import com.investigawarma.app.ui.screens.SplashScreen
import com.investigawarma.app.ui.screens.StatsScreen
import com.investigawarma.app.ui.screens.ZoneScreen
import com.investigawarma.app.ui.theme.ZoneBio
import com.investigawarma.app.ui.theme.ZoneDatos
import com.investigawarma.app.ui.theme.ZoneLaboratorio
import com.investigawarma.app.ui.theme.ZoneMuseo
import com.investigawarma.app.ui.theme.ZoneObservacion
import com.investigawarma.app.ui.theme.ZonePlaneta
import com.investigawarma.app.ui.viewmodel.ViewModelFactory

private fun zoneAccent(zone: Zone) = when (zone) {
    Zone.SALA_OBSERVACION -> ZoneObservacion
    Zone.LABORATORIO_EXPERIMENTAL -> ZoneLaboratorio
    Zone.BIODESCUBRIMIENTO -> ZoneBio
    Zone.PLANETA_TIERRA -> ZonePlaneta
    Zone.CENTRO_DE_DATOS -> ZoneDatos
    Zone.MUSEO_CIENTIFICO -> ZoneMuseo
}

@Composable
fun InvestigaWarmaNavGraph(container: AppContainer) {
    val factory = remember { ViewModelFactory(container) }
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val profile = container.playerRepository.getProfile()
        startDestination = if (profile?.onboardingCompleted == true) Routes.HOME else Routes.ONBOARDING
    }

    val destination = startDestination
    if (destination == null) {
        SplashScreen()
        return
    }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = destination) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(factory = factory, onFinished = {
                navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(
                factory = factory,
                onZoneClick = { zone -> navController.navigate(Routes.zone(zone.name)) },
                onJournalClick = { navController.navigate(Routes.JOURNAL) },
                onMuseumClick = { navController.navigate(Routes.MUSEUM) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(
            Routes.ZONE,
            arguments = listOf(navArgument("zoneName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val zoneName = backStackEntry.arguments?.getString("zoneName") ?: Zone.SALA_OBSERVACION.name
            val zone = runCatching { Zone.valueOf(zoneName) }.getOrDefault(Zone.SALA_OBSERVACION)
            ZoneScreen(
                factory = factory,
                zone = zone,
                accentColor = zoneAccent(zone),
                onBack = { navController.popBackStack() },
                onMissionClick = { missionId -> navController.navigate(Routes.mission(missionId)) },
                onStatsClick = { navController.navigate(Routes.STATS) },
            )
        }
        composable(
            Routes.MISSION,
            arguments = listOf(navArgument("missionId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val missionId = backStackEntry.arguments?.getString("missionId") ?: return@composable
            MissionScreen(
                factory = factory,
                missionId = missionId,
                onBack = { navController.popBackStack() },
                onMissionFinished = { navController.popBackStack() },
            )
        }
        composable(Routes.JOURNAL) {
            JournalScreen(factory = factory, onBack = { navController.popBackStack() })
        }
        composable(Routes.MUSEUM) {
            MuseumScreen(factory = factory, onBack = { navController.popBackStack() })
        }
        composable(Routes.STATS) {
            StatsScreen(factory = factory, onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                factory = factory,
                onBack = { navController.popBackStack() },
                onProgressReset = {
                    navController.navigate(Routes.ONBOARDING) { popUpTo(0) { inclusive = true } }
                },
            )
        }
    }
}
