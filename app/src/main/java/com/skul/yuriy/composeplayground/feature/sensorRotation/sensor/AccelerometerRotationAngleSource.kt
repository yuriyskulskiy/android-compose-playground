package com.skul.yuriy.composeplayground.feature.sensorRotation.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs
import kotlin.math.atan2

class AccelerometerRotationAngleSource(
    context: Context
) : RotationAngleSource {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var smoothedAngle: Float? = null
    private var isFlat = false
    private var listener: SensorEventListener? = null

    override fun start(onAngleChanged: (Float) -> Unit) {
        val accelerometer = accelerometer ?: return
        if (listener != null) return

        listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val shouldEnterFlat = abs(z) > FLAT_ENTER_Z_THRESHOLD &&
                    abs(x) < FLAT_XY_THRESHOLD &&
                    abs(y) < FLAT_XY_THRESHOLD
                val shouldExitFlat = abs(z) < FLAT_EXIT_Z_THRESHOLD

                isFlat = when {
                    isFlat && shouldExitFlat -> false
                    !isFlat && shouldEnterFlat -> true
                    else -> isFlat
                }

                if (isFlat) return

                val angle = Math.toDegrees(atan2(x.toDouble(), y.toDouble())).toFloat()
                // Seed the stream with the raw angle so the screen opens at the current tilt
                // instead of visually easing in from an arbitrary previous smoothed state.
                smoothedAngle = smoothedAngle?.let { previousAngle ->
                    smoothAngle(
                        previousAngle = previousAngle,
                        targetAngle = angle,
                        alpha = ANGLE_SMOOTHING_ALPHA
                    )
                } ?: angle
                onAngleChanged(smoothedAngle ?: angle)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(
            listener,
            accelerometer,
            ACCELEROMETER_SENSOR_DELAY
        )
    }

    override fun stop() {
        listener?.let(sensorManager::unregisterListener)
        listener = null
        smoothedAngle = null
    }
}
