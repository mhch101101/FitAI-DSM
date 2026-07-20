package app.dsm.fitai.data.repository

import app.dsm.fitai.data.local.dao.StepDao
import app.dsm.fitai.data.local.database.StepRecordEntity
import app.dsm.fitai.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

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

    override suspend fun seedDemoStepsIfEmpty() {
        val today = getTodayDate()
        // Only seed if there is no history before today. Today itself stays untouched
        // so the real sensor reading remains the source of truth for the current day.
        if (stepDao.countRecordsBefore(today) > 0) return

        val goal = stepDao.getStepsByDate(today)?.goal ?: 8000
        val calendar = Calendar.getInstance()

        for (daysAgo in 1..6) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val date = dateFormat.format(calendar.time)
            // lastSensorValue = 0f marks these as demo/history rows with no real
            // sensor baseline, so updateStepsAtomic won't chain a delta off them.
            stepDao.insertOrUpdateSteps(
                StepRecordEntity(
                    date = date,
                    steps = demoStepsForGoal(goal),
                    goal = goal,
                    lastSensorValue = 0f
                )
            )
        }
    }

    // Generates a plausible daily step count relative to the user's goal:
    // most days land between ~60% and ~110% of the goal, so the week looks
    // natural (some days short, some days meeting or beating the target)
    // and scales with each user's objective instead of a fixed range.
    private fun demoStepsForGoal(goal: Int): Int {
        val ratio = Random.nextDouble(0.6, 1.1)
        val jitter = Random.nextInt(-250, 251)
        return (goal * ratio + jitter).roundToInt().coerceAtLeast(0)
    }
}
