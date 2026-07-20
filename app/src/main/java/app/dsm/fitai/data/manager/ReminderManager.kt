package app.dsm.fitai.data.manager

import android.content.Context
import androidx.work.*
import app.dsm.fitai.data.worker.TrainingReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderManager(private val context: Context) {

    fun scheduleDailyTrainingReminder(hour: Int = 9, minute: Int = 0) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val timeDiff = calendar.timeInMillis - System.currentTimeMillis()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<TrainingReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    fun cancelReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(REMINDER_WORK_NAME)
    }

    companion object {
        private const val REMINDER_WORK_NAME = "training_reminder_work"
    }
}
