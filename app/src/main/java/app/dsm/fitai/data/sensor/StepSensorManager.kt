package app.dsm.fitai.data.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import app.dsm.fitai.domain.repository.StepRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

class StepSensorManager @Inject constructor(
    private val context: Context,
    private val stepRepository: StepRepository
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun hasPermission(): Boolean {
        // ACTIVITY_RECOGNITION is a runtime permission only from API 29 onwards.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startListening() {
        if (!hasPermission()) {
            Log.w("StepSensorManager", "ACTIVITY_RECOGNITION no concedido; no se registra el listener")
            return
        }
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
        scope.coroutineContext.cancelChildren()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0]
            scope.launch {
                try {
                    stepRepository.updateSteps(totalSteps)
                } catch (e: Exception) {
                    Log.e("StepSensorManager", "Error al actualizar pasos", e)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
