package app.dsm.fitai.di

import android.app.Application
import androidx.room.Room
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
        ).build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }
}