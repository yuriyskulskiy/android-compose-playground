package com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue

private const val ANGLE_SMOOTHING_ALPHA = 0.12f

/**
 * Low-pass rotation smoother based on a fixed alpha coefficient.
 *
 * Behavior:
 * - far from canonical angles, follows the raw stream with classic alpha smoothing
 * - near canonical angles, switches target from the raw value to the nearest anchor
 * - once close enough to that anchor, holds the exact anchor value until hysteresis releases it
 *
 * This strategy works best with dense, fine-grained sensor updates such as accelerometer-based
 * float angles.
 */
internal class AlphaRotationAngleSmoother : IRotationAngleSmoother {
    private var smoothedAngle: Float? = null
    private var mode: AlphaMode = AlphaMode.Free

    override var angle by mutableFloatStateOf(0f)
        private set

    override fun onRawAngle(rawAngle: Float) {
        val normalizedRawAngle = normalizeAnchoringDegrees(rawAngle)
        val previousAngle = smoothedAngle ?: normalizedRawAngle

        val targetAngle = when (val currentMode = mode) {
            AlphaMode.Free -> {
                val anchor = nearestAnchor(normalizedRawAngle)
                if (angularDistance(normalizedRawAngle, anchor) <= SNAP_ENTER_DEGREES) {
                    mode = AlphaMode.Snapping(anchor)
                    anchor
                } else {
                    normalizedRawAngle
                }
            }

            is AlphaMode.Snapping -> {
                val anchor = currentMode.anchor
                if (angularDistance(normalizedRawAngle, anchor) > SNAP_EXIT_DEGREES) {
                    mode = AlphaMode.Free
                    normalizedRawAngle
                } else {
                    anchor
                }
            }

            is AlphaMode.Snapped -> {
                val anchor = currentMode.anchor
                if (angularDistance(normalizedRawAngle, anchor) > SNAP_EXIT_DEGREES) {
                    mode = AlphaMode.Free
                    normalizedRawAngle
                } else {
                    anchor
                }
            }
        }

        var nextAngle = smoothedAngle?.let {
            smoothAngle(
                previousAngle = previousAngle,
                targetAngle = targetAngle,
                alpha = ANGLE_SMOOTHING_ALPHA
            )
        } ?: targetAngle

        val currentMode = mode
        if (currentMode is AlphaMode.Snapping &&
            angularDistance(nextAngle, currentMode.anchor) <= SNAP_SETTLE_DEGREES
        ) {
            mode = AlphaMode.Snapped(currentMode.anchor)
            nextAngle = currentMode.anchor
        }

        smoothedAngle = nextAngle
        angle = nextAngle
    }

    override fun reset() {
        smoothedAngle = null
        mode = AlphaMode.Free
        angle = 0f
    }
}

private sealed interface AlphaMode {
    data object Free : AlphaMode
    data class Snapping(val anchor: Float) : AlphaMode
    data class Snapped(val anchor: Float) : AlphaMode
}

private fun smoothAngle(
    previousAngle: Float,
    targetAngle: Float,
    alpha: Float
): Float {
    val delta = normalizeAnchoringDegrees(targetAngle - previousAngle)
    return normalizeAnchoringDegrees(previousAngle + delta * alpha)
}
