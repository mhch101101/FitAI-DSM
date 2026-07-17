package app.dsm.fitai.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.dsm.fitai.di.FitAIApp
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull

class StepSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), SensorEventListener {

    private val stepRepository = (context.applicationContext as FitAIApp).appComponent.stepRepository()
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    
    private val stepValueDeferred = CompletableDeferred<Float>()

    override suspend fun doWork(): Result {
        Log.d("StepSyncWorker", "Iniciando doWork para sincronización de pasos")
        if (!hasActivityRecognitionPermission()) {
            Log.e("StepSyncWorker", "Permiso ACTIVITY_RECOGNITION no concedido; se cancela la sincronización")
            return Result.failure()
        }
        if (stepCounterSensor == null) {
            Log.e("StepSyncWorker", "El sensor TYPE_STEP_COUNTER no está disponible en este dispositivo")
            return Result.failure()
        }

        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
        
        val sensorValue = withTimeoutOrNull(5000) {
            stepValueDeferred.await()
        }
        
        sensorManager.unregisterListener(this)

        return if (sensorValue != null) {
            Log.d("StepSyncWorker", "Valor del sensor recibido con éxito: $sensorValue")
            stepRepository.updateSteps(sensorValue)
            checkGoalReached()
            Result.success()
        } else {
            Log.w("StepSyncWorker", "No se recibió respuesta del sensor dentro del tiempo límite (5s)")
            Result.retry()
        }
    }

    private suspend fun checkGoalReached() {
        val today = stepRepository.getTodaySteps().firstOrNull()
        if (today != null) {
            Log.d("StepSyncWorker", "Pasos hoy: ${today.steps}/${today.goal}, notificado: ${today.notified}")
            if (today.steps >= today.goal && !today.notified) {
                Log.d("StepSyncWorker", "¡Meta alcanzada! Mostrando notificación")
                showNotification()
                stepRepository.markAsNotified(today.date)
            }
        }
    }

    private fun showNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("steps_channel", "Steps", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "steps_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Meta alcanzada!")
            .setContentText("Has logrado tu meta de pasos de hoy. ¡Buen trabajo!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(1, notification)
    }

    private fun hasActivityRecognitionPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceBoot = event.values[0]
            Log.d("StepSyncWorker", "onSensorChanged detectado. Valor acumulado del sensor: $totalStepsSinceBoot")
            stepValueDeferred.complete(totalStepsSinceBoot)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
