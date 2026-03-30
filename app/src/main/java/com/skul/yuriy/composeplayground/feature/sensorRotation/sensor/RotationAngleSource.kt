package com.skul.yuriy.composeplayground.feature.sensorRotation.sensor

interface RotationAngleSource {
    fun start(onAngleChanged: (Float) -> Unit)
    fun stop()
}

enum class RotationAngleSourceType {
    Accelerometer,
    OrientationEventListener
}

internal fun smoothAngle(
    previousAngle: Float,
    targetAngle: Float,
    alpha: Float
): Float {
    val delta = normalizeDegrees(targetAngle - previousAngle)
    return normalizeDegrees(previousAngle + delta * alpha)
}

internal fun normalizeDegrees(angle: Float): Float {
    val normalized = angle % 360f
    return when {
        normalized > 180f -> normalized - 360f
        normalized <= -180f -> normalized + 360f
        else -> normalized
    }
}

internal const val FLAT_ENTER_Z_THRESHOLD = 8.7f
internal const val FLAT_EXIT_Z_THRESHOLD = 8.0f
internal const val FLAT_XY_THRESHOLD = 2.0f
internal const val ANGLE_SMOOTHING_ALPHA = 0.12f
internal const val ACCELEROMETER_SENSOR_DELAY = android.hardware.SensorManager.SENSOR_DELAY_GAME
internal const val ORIENTATION_EVENT_SENSOR_DELAY = android.hardware.SensorManager.SENSOR_DELAY_GAME
