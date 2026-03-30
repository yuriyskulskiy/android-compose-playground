package com.skul.yuriy.composeplayground.feature.sensorRotation.sensor

import android.content.Context
import android.util.Log
import android.view.OrientationEventListener

class OrientationEventRotationAngleSource(
    context: Context
) : RotationAngleSource {
    private var smoothedAngle = 0f
    private val orientationListener = object : OrientationEventListener(
        context,
        ORIENTATION_EVENT_SENSOR_DELAY
    ) {
        private var onAngleChanged: ((Float) -> Unit)? = null

        fun bind(onAngleChanged: (Float) -> Unit) {
            this.onAngleChanged = onAngleChanged
        }

        override fun onOrientationChanged(orientation: Int) {
            if (orientation == ORIENTATION_UNKNOWN) return

            val rawAngle = if (orientation == 0) 0f else 360f - orientation.toFloat()
            val angle = normalizeDegrees(rawAngle - 90f)
            smoothedAngle = smoothAngle(
                previousAngle = smoothedAngle,
                targetAngle = angle,
                alpha = ANGLE_SMOOTHING_ALPHA
            )
            onAngleChanged?.invoke(smoothedAngle)
        }
    }

    override fun start(onAngleChanged: (Float) -> Unit) {
        orientationListener.bind(onAngleChanged)
        runCatching { orientationListener.enable() }
            .onFailure { error ->
                Log.e(
                    "SensorRotationActivity",
                    "Failed to enable orientation listener with delay=$ORIENTATION_EVENT_SENSOR_DELAY",
                    error
                )
            }
    }

    override fun stop() {
        orientationListener.disable()
    }
}
