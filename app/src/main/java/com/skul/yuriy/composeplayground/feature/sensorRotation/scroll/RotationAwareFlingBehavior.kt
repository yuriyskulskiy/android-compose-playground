package com.skul.yuriy.composeplayground.feature.sensorRotation.scroll

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal fun rememberRotationAwareFlingBehavior(
    angleDegrees: Float,
): FlingBehavior {
    val defaultFlingBehavior = ScrollableDefaults.flingBehavior()
    return remember(defaultFlingBehavior, angleDegrees) {
        RotationAwareFlingBehavior(
            delegate = defaultFlingBehavior,
            angleDegrees = angleDegrees,
        )
    }
}

private class RotationAwareFlingBehavior(
    private val delegate: FlingBehavior,
    private val angleDegrees: Float,
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        val adjustedVelocity =
            if (isRotationScrollDirectionInverted(angleDegrees)) {
                -initialVelocity
            } else {
                initialVelocity
            }
        return with(delegate) { performFling(adjustedVelocity) }
    }
}
