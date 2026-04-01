package com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.normalizeDegrees
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class AnimatedRotationAngleSmoother(
    private val scope: CoroutineScope
) : IRotationAngleSmoother {
    private val animatable = Animatable(0f)
    private var isInitialized = false

    override var angle by mutableFloatStateOf(0f)
        private set

    override fun onRawAngle(rawAngle: Float) {
        scope.launch {
            if (!isInitialized) {
                isInitialized = true
                animatable.snapTo(rawAngle)
                angle = rawAngle
                return@launch
            }

            animatable.animateRotationToShortestPath(
                targetValue = rawAngle,
                block = { angle = normalizeDegrees(value) }
            )
        }
    }

    override fun reset() {
        isInitialized = false
        angle = 0f
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.animateRotationToShortestPath(
    targetValue: Float,
    block: Animatable<Float, AnimationVector1D>.() -> Unit,
) {
    val currentValue = normalizeDegrees(value)
    val diff = normalizeDegrees(targetValue - currentValue)
    val adjustedTargetValue = value + diff
    animateTo(
        targetValue = adjustedTargetValue,
        animationSpec = spring(
            stiffness = 150f,
            visibilityThreshold = 0.1f
        ),
        block = block
    )
}
