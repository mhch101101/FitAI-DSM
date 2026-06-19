package app.dsm.fitai.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "routine_days",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoutineDayEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "routine_id") val routineId: String,
    @ColumnInfo(name = "day_name") val dayName: String
)
