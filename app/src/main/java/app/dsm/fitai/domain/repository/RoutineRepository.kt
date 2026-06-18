package app.dsm.fitai.domain.repository

import app.dsm.fitai.domain.model.Routine

interface RoutineRepository {
    suspend fun generateDefaultRoutine(userId: String, objective: String): Routine

    suspend fun generateRoutine(userId:String,objective: String,experienceLevel: String,daysPerWeek: Int,sessionDuration: Int): Routine
    suspend fun getRoutine(userId: String): Routine?
    suspend fun saveRoutine(routine: Routine)
}
