package app.dsm.fitai.data.repository

import app.dsm.fitai.data.firebase.RoutineFirestore
import app.dsm.fitai.data.remote.api.RoutineApi
import app.dsm.fitai.data.remote.dto.GenerateRoutineRequestDto
import app.dsm.fitai.data.remote.mapper.toDomain
import app.dsm.fitai.domain.model.DayRoutine
import app.dsm.fitai.domain.model.Exercise
import app.dsm.fitai.domain.model.Routine
import app.dsm.fitai.domain.repository.RoutineRepository
import java.util.Date
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineFirestore: RoutineFirestore,
    private val api: RoutineApi
) : RoutineRepository {

    override suspend fun generateRoutine(
        userId:String,
        objective: String,
        experienceLevel: String,
        daysPerWeek: Int,
        sessionDuration: Int
    ): Routine {

        val response = api.generateRoutine(
            GenerateRoutineRequestDto(
                objective = objective,
                experienceLevel = experienceLevel,
                daysPerWeek = daysPerWeek,
                sessionDuration = sessionDuration
            )
        )

        if (!response.success || response.routine == null) {
            throw Exception("No se pudo generar la rutina")
        }
        val routineResponse = response.routine
            .toDomain()
            .copy(
                userId = userId,
                createdAt = Date(),
                isActive = true
            )
        return routineResponse
    }

    override suspend fun generateDefaultRoutine(userId: String, objective: String): Routine {
        val routine = Routine(
            userId = userId,
            name = "Rutina Inicial de $objective",
            objective = objective,
            isActive = true,
            createdAt = Date(),
            days = listOf(
                DayRoutine(
                    dayName = "Día 1 - Empuje (Pecho/Tríceps/Hombro)",
                    exercises = listOf(
                        Exercise("Press de Banca", "Pecho", 3, "10-12", 90, 40.0),
                        Exercise("Press Militar", "Hombro", 3, "10", 90, 20.0),
                        Exercise("Extensiones de Tríceps", "Tríceps", 3, "12", 60, 15.0)
                    )
                ),
                DayRoutine(
                    dayName = "Día 2 - Tracción (Espalda/Bíceps)",
                    exercises = listOf(
                        Exercise("Jalón al Pecho", "Espalda", 3, "10", 90, 45.0),
                        Exercise("Remo con Mancuerna", "Espalda", 3, "12", 90, 18.0),
                        Exercise("Curl de Bíceps", "Bíceps", 3, "12", 60, 10.0)
                    )
                ),
                DayRoutine(
                    dayName = "Día 3 - Pierna",
                    exercises = listOf(
                        Exercise("Sentadilla", "Pierna", 3, "10", 120, 50.0),
                        Exercise("Peso Muerto Rumano", "Pierna", 3, "12", 120, 40.0),
                        Exercise("Prensa de Piernas", "Pierna", 3, "12", 90, 80.0)
                    )
                )
            )
        )
        saveRoutine(routine)
        return routine
    }

    override suspend fun getRoutine(userId: String): Routine? {
        return routineFirestore.getRoutine(userId)
    }

    override suspend fun saveRoutine(routine: Routine) {
        routineFirestore.saveRoutine(routine)
    }
}
