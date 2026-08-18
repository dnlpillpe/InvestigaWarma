package com.investigawarma.app.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val ZONE = "zone/{zoneName}"
    const val MISSION = "mission/{missionId}"
    const val JOURNAL = "journal"
    const val MUSEUM = "museum"
    const val STATS = "stats"
    const val SETTINGS = "settings"

    fun zone(zoneName: String) = "zone/$zoneName"
    fun mission(missionId: String) = "mission/$missionId"
}
