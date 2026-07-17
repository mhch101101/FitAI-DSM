package app.dsm.fitai.data.repository

import app.dsm.fitai.data.local.dao.StepDao
import app.dsm.fitai.data.local.database.StepRecordEntity
import app.dsm.fitai.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class StepRepositoryImpl @Inject constructor(
    private val stepDao: StepDao
) : StepRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun getTodayDate(): String = dateFormat.format(Date())

    override fun getTodaySteps(): Flow<StepRecordEntity?> {
        return stepDao.getStepsFlowByDate(getTodayDate())
    }

    override suspend fun updateSteps(steps: Int, lastSensorValue: Float) {
        val today = getTodayDate()
        val existing = stepDao.getStepsByDate(today)
        
        val record = if (existing != null) {
            existing.copy(steps = steps, lastSensorValue = lastSensorValue)
        } else {
            // Default goal if not set yet, though it should be set by setStepGoal
            StepRecordEntity(today, steps, 8000, lastSensorValue)
        }
        stepDao.insertOrUpdateSteps(record)
    }

    override suspend fun setStepGoal(goal: Int) {
        val today = getTodayDate()
        val existing = stepDao.getStepsByDate(today)
        val record = if (existing != null) {
            existing.copy(goal = goal)
        } else {
            StepRecordEntity(today, 0, goal, 0f)
        }
        stepDao.insertOrUpdateSteps(record)
    }

    override fun getWeeklyHistory(): Flow<List<StepRecordEntity>> {
        return stepDao.getWeeklySteps()
    }

    override fun getRecommendedGoal(objective: String): Int {
        return when (objective.lowercase()) {
            "hipertrofia" -> 8000
            "fuerza" -> 7000
            "mixto" -> 10000
            else -> 9000
        }
    }
}
