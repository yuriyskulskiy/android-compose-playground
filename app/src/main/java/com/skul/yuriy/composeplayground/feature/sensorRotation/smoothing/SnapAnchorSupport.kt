package com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing

import kotlin.math.abs

/**
 * Shared angle-anchoring helpers used by rotation smoothers near canonical angles.
 *
 * The current strategy uses a small hysteresis window around the cardinal anchors:
 * - enter snap zone at [SNAP_ENTER_DEGREES]
 * - stay snapped until [SNAP_EXIT_DEGREES]
 * - switch from "snapping" to fully "snapped" when the angle is within [SNAP_SETTLE_DEGREES]
 */
internal const val SNAP_ENTER_DEGREES = 5f
internal const val SNAP_EXIT_DEGREES = 10f
internal const val SNAP_SETTLE_DEGREES = 0.75f

internal fun nearestAnchor(angle: Float): Float {
    val anchors = floatArrayOf(-180f, -90f, 0f, 90f, 180f)
    return anchors.minBy { angularDistance(angle, it) }
}

internal fun angularDistance(angle: Float, anchor: Float): Float =
    abs(normalizeAnchoringDegrees(angle - anchor))

internal fun normalizeAnchoringDegrees(angle: Float): Float {
    val normalized = angle % 360f
    return when {
        normalized > 180f -> normalized - 360f
        normalized <= -180f -> normalized + 360f
        else -> normalized
    }
}
