package app.dsm.fitai.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.work.*
import app.dsm.fitai.data.worker.StepSyncWorker
import app.dsm.fitai.data.worker.SyncWorker
import java.util.concurrent.TimeUnit

class FitAIApp: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory()
            .create(this)

        scheduleSync()
        scheduleStepsSync()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Step Notifications"
            val descriptionText = "Notifications for step goals"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("steps_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "room_firestore_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    private fun scheduleStepsSync() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            return
        }

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<StepSyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "steps_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}