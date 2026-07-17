package app.dsm.fitai.domain.repository

import app.dsm.fitai.data.local.database.StepRecordEntity
import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getTodaySteps(): Flow<StepRecordEntity?>
    suspend fun updateSteps(steps: Int, lastSensorValue: Float)
    suspend fun setStepGoal(goal: Int)
    fun getWeeklyHistory(): Flow<List<StepRecordEntity>>
    fun getRecommendedGoal(objective: String): Int
}
