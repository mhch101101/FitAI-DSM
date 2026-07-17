package app.dsm.fitai.domain.repository

import app.dsm.fitai.data.local.database.ExerciseEntity
import app.dsm.fitai.domain.model.OneRmPoint
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun getStrengthProgression(exerciseId: String): Flow<List<OneRmPoint>>
    fun getLoggedExercises(): Flow<List<ExerciseEntity>>
    suspend fun seedDemoDataIfEmpty(userId: String)
}
