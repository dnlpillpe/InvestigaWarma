package com.investigawarma.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class InvestigaWarmaApp : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // Primer arranque: puebla la base de datos vacía con el contenido inicial.
        applicationScope.launch {
            container.seeder.seedIfNeeded()
        }
    }
}
