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

    override suspend fun updateSteps(rawSensorValue: Float) {
        stepDao.updateStepsAtomic(getTodayDate(), rawSensorValue)
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

    override suspend fun markAsNotified(date: String) {
        val existing = stepDao.getStepsByDate(date)
        if (existing != null) {
            stepDao.insertOrUpdateSteps(existing.copy(notified = true))
        }
    }
}
