package com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Rotation smoother based on a single long-lived [Animatable].
 *
 * Behavior:
 * - far from canonical angles, animates toward the latest raw angle
 * - near canonical angles, changes the animation target to the nearest anchor
 * - once close enough, snaps into an exact anchored value until hysteresis releases it
 *
 * This strategy behaves best with coarser, quantized angle sources such as
 * `OrientationEventListener`, where larger discrete steps are naturally turned into smooth motion.
 */
internal class AnimatedRotationAngleSmoother(
    private val scope: CoroutineScope
) : IRotationAngleSmoother {
    private val animatable = Animatable(0f)
    private var isInitialized = false
    private var mode: AnimatedMode = AnimatedMode.Free

    override var angle by mutableFloatStateOf(0f)
        private set

    override fun onRawAngle(rawAngle: Float) {
        val normalizedRawAngle = normalizeAnchoringDegrees(rawAngle)

        scope.launch {
            if (!isInitialized) {
                isInitialized = true
                mode = AnimatedMode.Free
                animatable.snapTo(normalizedRawAngle)
                angle = normalizedRawAngle
                return@launch
            }

            val targetAngle = when (val currentMode = mode) {
                AnimatedMode.Free -> {
                    val anchor = nearestAnchor(normalizedRawAngle)
                    if (angularDistance(normalizedRawAngle, anchor) <= SNAP_ENTER_DEGREES) {
                        mode = AnimatedMode.Snapping(anchor)
                        anchor
                    } else {
                        normalizedRawAngle
                    }
                }

                is AnimatedMode.Snapping -> {
                    val anchor = currentMode.anchor
                    if (angularDistance(normalizedRawAngle, anchor) > SNAP_EXIT_DEGREES) {
                        mode = AnimatedMode.Free
                        normalizedRawAngle
                    } else {
                        anchor
                    }
                }

                is AnimatedMode.Snapped -> {
                    val anchor = currentMode.anchor
                    if (angularDistance(normalizedRawAngle, anchor) > SNAP_EXIT_DEGREES) {
                        mode = AnimatedMode.Free
                        normalizedRawAngle
                    } else {
                        angle = anchor
                        animatable.snapTo(anchor)
                        return@launch
                    }
                }
            }

            animatable.animateRotationToShortestPath(
                targetValue = targetAngle,
                block = {
                    val normalizedValue = normalizeAnchoringDegrees(value)
                    angle = normalizedValue

                    val currentMode = mode
                    if (currentMode is AnimatedMode.Snapping &&
                        angularDistance(normalizedValue, currentMode.anchor) <= SNAP_SETTLE_DEGREES
                    ) {
                        mode = AnimatedMode.Snapped(currentMode.anchor)
                        angle = currentMode.anchor
                    }
                }
            )
        }
    }

    override fun reset() {
        isInitialized = false
        mode = AnimatedMode.Free
        angle = 0f
    }
}

private sealed interface AnimatedMode {
    data object Free : AnimatedMode
    data class Snapping(val anchor: Float) : AnimatedMode
    data class Snapped(val anchor: Float) : AnimatedMode
}

private suspend fun Animatable<Float, AnimationVector1D>.animateRotationToShortestPath(
    targetValue: Float,
    block: Animatable<Float, AnimationVector1D>.() -> Unit,
) {
    val currentValue = normalizeAnchoringDegrees(value)
    val diff = normalizeAnchoringDegrees(targetValue - currentValue)
    val adjustedTargetValue = value + diff
    animateTo(
        targetValue = adjustedTargetValue,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = 500f,
            visibilityThreshold = 0.1f
        ),
        block = block
    )
}
