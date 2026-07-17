package app.dsm.fitai.data.local.dao

import androidx.room.*
import app.dsm.fitai.data.local.database.StepRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {
    @Query("SELECT * FROM step_records WHERE date = :date")
    suspend fun getStepsByDate(date: String): StepRecordEntity?

    @Query("SELECT * FROM step_records WHERE date = :date")
    fun getStepsFlowByDate(date: String): Flow<StepRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSteps(record: StepRecordEntity)

    @Query("SELECT * FROM step_records ORDER BY date DESC LIMIT 7")
    fun getWeeklySteps(): Flow<List<StepRecordEntity>>

    @Query("SELECT * FROM step_records ORDER BY date DESC LIMIT 1")
    suspend fun getLatestRecord(): StepRecordEntity?

    // Atomic read-modify-write: the live sensor listener and StepSyncWorker can
    // both call this concurrently, so the delta calculation must run in a single
    // transaction to avoid lost updates.
    @Transaction
    suspend fun updateStepsAtomic(today: String, rawSensorValue: Float) {
        val existing = getStepsByDate(today)
        val latestRecord = getLatestRecord()

        val record = if (latestRecord == null) {
            // First time the app has ever synced steps.
            // We set today's steps to 0 to avoid a sudden jump, and record the current raw sensor value.
            if (existing != null) {
                existing.copy(steps = 0, lastSensorValue = rawSensorValue)
            } else {
                StepRecordEntity(today, 0, 8000, rawSensorValue)
            }
        } else {
            val delta = if (rawSensorValue < latestRecord.lastSensorValue) {
                // Sensor counter reset (device reboot): the raw value itself is the delta.
                rawSensorValue
            } else {
                rawSensorValue - latestRecord.lastSensorValue
            }

            if (existing != null) {
                existing.copy(steps = existing.steps + delta.toInt(), lastSensorValue = rawSensorValue)
            } else {
                // First sync of a new day! We carry over a default or recommended goal, e.g. from the latest record.
                val goal = latestRecord.goal
                StepRecordEntity(today, delta.toInt(), goal, rawSensorValue)
            }
        }
        insertOrUpdateSteps(record)
    }
}
