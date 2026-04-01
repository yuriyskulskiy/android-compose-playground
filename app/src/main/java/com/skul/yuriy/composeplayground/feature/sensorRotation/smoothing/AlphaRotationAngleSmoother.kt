package com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.normalizeDegrees

private const val ANGLE_SMOOTHING_ALPHA = 0.12f

internal class AlphaRotationAngleSmoother : IRotationAngleSmoother {
    private var smoothedAngle: Float? = null

    override var angle by mutableFloatStateOf(0f)
        private set

    override fun onRawAngle(rawAngle: Float) {
        val nextAngle = smoothedAngle?.let { previousAngle ->
            smoothAngle(
                previousAngle = previousAngle,
                targetAngle = rawAngle,
                alpha = ANGLE_SMOOTHING_ALPHA
            )
        } ?: rawAngle

        smoothedAngle = nextAngle
        angle = nextAngle
    }

    override fun reset() {
        smoothedAngle = null
        angle = 0f
    }
}

private fun smoothAngle(
    previousAngle: Float,
    targetAngle: Float,
    alpha: Float
): Float {
    val delta = normalizeDegrees(targetAngle - previousAngle)
    return normalizeDegrees(previousAngle + delta * alpha)
}
