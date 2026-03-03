package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agslsimple

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Simple AGSL rectangular halo border.
 *
 * Inputs are intentionally minimal:
 * - [color] is the single tint used for both idle and pressed halo.
 * - [maxHaloBorderWidth] is the outer halo reach in pressed state.
 *
 * Internal profile (fixed):
 * - Idle: `0..2dp = 0.5 alpha`, `2..8dp -> 0 alpha`.
 * - Pressed: `0..2dp = 1 alpha`, `2..6dp -> 0.5 alpha`, `6..maxHaloBorderWidth -> 0 alpha`.
 * - Transition between idle and pressed is animated with `tween(300ms)`.
 */
@Composable
fun SimpleAgslBorderRectShadowBox(
    modifier: Modifier = Modifier,
    color: Color = Color(red = 0.10f, green = 0.30f, blue = 1.00f, alpha = 1f),
    cornerRadius: Dp = 24.dp,
    maxHaloBorderWidth: Dp = 32.dp
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedIntensity by animateFloatAsState(
        targetValue = if (isPressed) 1.6f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )
    val animatedPress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
            .drawOutlineSimpleAgslShadow(
                color = color,
                cornerRadius = cornerRadius,
                maxHaloBorderWidth = maxHaloBorderWidth,
                intensity = animatedIntensity,
                press = animatedPress
            )
    )
}
