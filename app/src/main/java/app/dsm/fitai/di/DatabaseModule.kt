package app.dsm.fitai.di

import android.app.Application
import androidx.room.Room
import app.dsm.fitai.data.local.dao.ExerciseDao
import app.dsm.fitai.data.local.dao.RoutineDao
import app.dsm.fitai.data.local.dao.UserDao
import app.dsm.fitai.data.local.entities.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "fitai_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    fun provideRoutineDao(db: AppDatabase): RoutineDao {
        return db.routineDao()
    }

    @Provides
    fun provideExerciseDao(db: AppDatabase): ExerciseDao {
        return db.exerciseDao()
    }

    @Provides
    fun provideStepDao(db: AppDatabase): app.dsm.fitai.data.local.dao.StepDao {
        return db.stepDao()
    }

    @Provides
    fun provideTrainingDao(db: AppDatabase): app.dsm.fitai.data.local.dao.TrainingDao {
        return db.trainingDao()
    }
}
