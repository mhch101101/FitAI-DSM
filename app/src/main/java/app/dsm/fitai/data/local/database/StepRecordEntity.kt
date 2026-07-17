package app.dsm.fitai.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_records")
data class StepRecordEntity(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    val steps: Int,
    val goal: Int,
    val lastSensorValue: Float // To calculate delta if sensor doesn't reset
)
