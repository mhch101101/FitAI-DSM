package app.dsm.fitai.di

import android.app.Application
import androidx.work.*
import app.dsm.fitai.data.worker.SyncWorker
import java.util.concurrent.TimeUnit

class FitAIApp: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory()
            .create(this)
    }

    private fun scheduleSync() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                NetworkType.CONNECTED
            )
            .build()

        val request =
            PeriodicWorkRequestBuilder<SyncWorker>(
                6,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "room_firestore_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

}