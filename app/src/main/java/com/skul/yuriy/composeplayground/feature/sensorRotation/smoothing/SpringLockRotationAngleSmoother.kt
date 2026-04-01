package com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val SPRING_ENTER_DEGREES = 20f
private const val SPRING_EXIT_DEGREES = 24f
private const val LOCK_ENTER_DEGREES = 10f
private const val LOCK_EXIT_DEGREES = 20f

/**
 * Experimental anchor smoother with three phases:
 * - free motion
 * - spring attraction near a canonical angle
 * - hard lock when close enough to the anchor
 *
 * This is a more physical alternative to the simpler hysteresis+snap approaches. It is kept as a
 * separate experiment because the extra spring phase can still feel too dynamic for some UIs.
 */
internal class SpringLockRotationAngleSmoother(
    private val scope: CoroutineScope
) : IRotationAngleSmoother {
    private val animatable = Animatable(0f)
    private var isInitialized = false
    private var mode: Mode = Mode.Free

    override var angle by mutableFloatStateOf(0f)
        private set

    override fun onRawAngle(rawAngle: Float) {
        val normalizedRawAngle = normalizeAnchoringDegrees(rawAngle)
        if (!isInitialized) {
            isInitialized = true
            mode = Mode.Free
            angle = normalizedRawAngle
            scope.launch { animatable.snapTo(normalizedRawAngle) }
            return
        }

        val nearestAnchor = nearestAnchor(normalizedRawAngle)
        val distanceToAnchor = angularDistance(normalizedRawAngle, nearestAnchor)

        when (val currentMode = mode) {
            Mode.Free -> {
                if (distanceToAnchor <= SPRING_ENTER_DEGREES) {
                    mode = Mode.Spring(anchor = nearestAnchor)
                    animateToAnchor(nearestAnchor)
                } else {
                    angle = normalizedRawAngle
                    scope.launch { animatable.snapTo(normalizedRawAngle) }
                }
            }

            is Mode.Spring -> {
                val anchor = currentMode.anchor
                val distanceToCurrentAnchor = angularDistance(normalizedRawAngle, anchor)

                when {
                    distanceToCurrentAnchor <= LOCK_ENTER_DEGREES -> {
                        mode = Mode.Locked(anchor = anchor)
                        angle = anchor
                        scope.launch { animatable.snapTo(anchor) }
                    }

                    distanceToCurrentAnchor > SPRING_EXIT_DEGREES -> {
                        mode = Mode.Free
                        angle = normalizedRawAngle
                        scope.launch { animatable.snapTo(normalizedRawAngle) }
                    }

                    else -> animateToAnchor(anchor)
                }
            }

            is Mode.Locked -> {
                val anchor = currentMode.anchor
                val distanceToCurrentAnchor = angularDistance(normalizedRawAngle, anchor)

                when {
                    distanceToCurrentAnchor <= LOCK_EXIT_DEGREES -> {
                        angle = anchor
                        scope.launch { animatable.snapTo(anchor) }
                    }

                    distanceToCurrentAnchor <= SPRING_EXIT_DEGREES -> {
                        mode = Mode.Spring(anchor = anchor)
                        animateToAnchor(anchor)
                    }

                    else -> {
                        mode = Mode.Free
                        angle = normalizedRawAngle
                        scope.launch { animatable.snapTo(normalizedRawAngle) }
                    }
                }
            }
        }
    }

    override fun reset() {
        isInitialized = false
        mode = Mode.Free
        angle = 0f
    }

    private fun animateToAnchor(anchor: Float) {
        scope.launch {
            animatable.animateRotationToShortestPath(
                targetValue = anchor,
                block = { angle = normalizeAnchoringDegrees(value) }
            )
        }
    }
}

private sealed interface Mode {
    data object Free : Mode
    data class Spring(val anchor: Float) : Mode
    data class Locked(val anchor: Float) : Mode
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
            stiffness = 350f,
            visibilityThreshold = 0.1f
        ),
        block = block
    )
}
