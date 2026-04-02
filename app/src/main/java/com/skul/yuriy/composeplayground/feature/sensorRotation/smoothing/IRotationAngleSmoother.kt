package com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing

/**
 * Stateful strategy that converts a stream of raw device angles into a UI-friendly angle.
 *
 * Implementations are free to:
 * - smooth the signal over time
 * - animate to new targets
 * - anchor near canonical angles such as 0/90/180/-90
 *
 * The interface intentionally exposes only the current processed [angle] and accepts raw updates
 * through [onRawAngle], so different strategies remain interchangeable inside Compose.
 */
interface IRotationAngleSmoother {
    val hasAngle: Boolean

    val angle: Float

    fun onRawAngle(rawAngle: Float)

    fun reset()
}
