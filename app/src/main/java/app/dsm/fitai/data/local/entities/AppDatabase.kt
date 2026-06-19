package app.dsm.fitai.data.local.entities

import androidx.room.Database
import androidx.room.RoomDatabase
import app.dsm.fitai.data.local.dao.UserDao
import app.dsm.fitai.data.local.database.*

@Database(
    entities = [
        UserEntity::class,
        RoutineEntity::class,
        RoutineDayEntity::class,
        ExerciseEntity::class,
        RoutineExerciseEntity::class
    ],
    version = 4,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
