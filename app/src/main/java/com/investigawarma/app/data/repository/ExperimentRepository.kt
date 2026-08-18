package com.investigawarma.app.data.repository

import com.investigawarma.app.data.local.AppDatabase
import com.investigawarma.app.data.local.entity.ExperimentResultEntity
import com.investigawarma.app.domain.logic.MovementSimulatorEngine
import com.investigawarma.app.domain.logic.PlantSimulatorEngine
import com.investigawarma.app.domain.logic.TemperatureSimulatorEngine
import com.investigawarma.app.domain.model.SimulatorType
import kotlinx.coroutines.flow.Flow

class ExperimentRepository(private val db: AppDatabase) {

    fun observeByZone(zone: String) = db.experimentDao().observeByZone(zone)

    suspend fun getExperiment(id: String) = db.experimentDao().getById(id)

    suspend fun getParameters(experimentId: String) = db.experimentDao().getParameters(experimentId)

    fun observeResults(experimentId: String): Flow<List<ExperimentResultEntity>> =
        db.experimentResultDao().observeByExperiment(experimentId)

    /**
     * Ejecuta el experimento con los parámetros elegidos por el jugador usando el
     * motor real correspondiente, persiste el resultado y lo devuelve.
     */
    suspend fun runExperiment(
        experimentId: String,
        simulatorType: SimulatorType,
        parameters: Map<String, Float>,
    ): ExperimentResultEntity {
        val (summary, score) = when (simulatorType) {
            SimulatorType.PLANT -> {
                val r = PlantSimulatorEngine.simulate(
                    light = parameters["luz"] ?: 50f,
                    water = parameters["agua"] ?: 50f,
                    days = parameters["dias"] ?: 7f,
                )
                r.summary to (r.healthPercent / 100f)
            }
            SimulatorType.MOVEMENT -> {
                val r = MovementSimulatorEngine.simulate(
                    surfaceIndex = (parameters["superficie"] ?: 1f).toInt(),
                    weightGrams = parameters["peso"] ?: 100f,
                    forceNewtons = parameters["fuerza"] ?: 10f,
                )
                r.summary to (r.distanceMeters / 50f).coerceIn(0f, 1f)
            }
            SimulatorType.TEMPERATURE -> {
                val r = TemperatureSimulatorEngine.simulate(
                    initialTemp = parameters["temperaturaInicial"] ?: 20f,
                    insulation = parameters["aislamiento"] ?: 0.5f,
                    minutes = parameters["minutos"] ?: 10f,
                )
                r.summary to (parameters["aislamiento"] ?: 0.5f)
            }
            SimulatorType.NONE -> "Experimento completado." to 0.5f
        }

        val result = ExperimentResultEntity(
            experimentId = experimentId,
            parameters = parameters,
            outcomeSummary = summary,
            outcomeScore = score,
            createdAt = System.currentTimeMillis(),
        )
        val id = db.experimentResultDao().insert(result)
        return result.copy(id = id)
    }

    suspend fun resultsCount(): Int = db.experimentResultDao().count()
}
