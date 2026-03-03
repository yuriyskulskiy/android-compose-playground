package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agsladvanced

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FireShaderDraftRectShadowBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    bandWidth: Dp = 36.dp,
    contourWidth: Dp = 220.dp,
    contourHeight: Dp = 120.dp,
    bandScale: Float = 1f,
    smokeScale: Float = 1f,
    intensity: Float = 1f
) {
    var isPressed by remember { mutableStateOf(false) }
    var activeQuadrant by remember { mutableStateOf<PressQuadrant?>(null) }

    val animatedPressBand by animateFloatAsState(
        targetValue = if (isPressed && activeQuadrant == PressQuadrant.BottomLeft) 2.05f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressIntensity by animateFloatAsState(
        targetValue = if (isPressed && activeQuadrant == PressQuadrant.TopLeft) 2.55f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressSmoke by animateFloatAsState(
        targetValue = if (isPressed && activeQuadrant == PressQuadrant.TopRight) 3.2f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressSmokeOpacity by animateFloatAsState(
        targetValue = if (isPressed && activeQuadrant == PressQuadrant.TopRight) 2.0f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressBottomRightBandThin by animateFloatAsState(
        targetValue = if (isPressed && activeQuadrant == PressQuadrant.BottomRight) 1.7f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressBottomRightIntensity by animateFloatAsState(
        targetValue = if (isPressed && activeQuadrant == PressQuadrant.BottomRight) 3.0f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressBottomRightSmokeTight by animateFloatAsState(
        targetValue = if (isPressed && activeQuadrant == PressQuadrant.BottomRight) 1.15f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressBottomRightSmokeOpacity by animateFloatAsState(
        targetValue = if (isPressed && activeQuadrant == PressQuadrant.BottomRight) 1.2f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressBottomRightThinMode by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressCoreScale by animateFloatAsState(
        targetValue = when {
            isPressed && activeQuadrant == PressQuadrant.TopLeft -> 1.55f
            isPressed && activeQuadrant == PressQuadrant.TopRight -> 0.82f
            isPressed && activeQuadrant == PressQuadrant.BottomRight -> 1.25f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressSmokeBlueTint by animateFloatAsState(
        targetValue = when {
            isPressed && activeQuadrant == PressQuadrant.TopRight -> 0.8f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 220),
        label = ""
    )

    val interactiveBand = (bandWidth * bandScale * animatedPressBand * animatedPressBottomRightBandThin)
        .coerceIn(2.dp, 92.dp)
    val interactiveCorner = cornerRadius.coerceIn(8.dp, 56.dp)
    val interactiveIntensity = (intensity * animatedPressIntensity * animatedPressBottomRightIntensity)
        .coerceIn(0.2f, 3.4f)
    val interactiveSmoke = (smokeScale * animatedPressSmoke * animatedPressBottomRightSmokeTight)
        .coerceIn(0.18f, 4.6f)
    val interactiveSmokeOpacity = (animatedPressSmokeOpacity * animatedPressBottomRightSmokeOpacity)
        .coerceIn(0.14f, 6f)

    val transition = rememberInfiniteTransition(label = "")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60_000, easing = LinearEasing)
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val centerX = size.width * 0.5f
                    val centerY = size.height * 0.5f
                    activeQuadrant = when {
                        down.position.x < centerX && down.position.y < centerY -> PressQuadrant.TopLeft
                        down.position.x >= centerX && down.position.y < centerY -> PressQuadrant.TopRight
                        down.position.x < centerX && down.position.y >= centerY -> PressQuadrant.BottomLeft
                        else -> PressQuadrant.BottomRight
                    }
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                    activeQuadrant = null
                }
            }
            .fireRectHaloShaderDraft(
                time = time,
                bandWidth = interactiveBand,
                cornerRadius = interactiveCorner,
                contourWidth = contourWidth,
                contourHeight = contourHeight,
                smokeScale = interactiveSmoke,
                intensity = interactiveIntensity,
                smokeOpacity = interactiveSmokeOpacity,
                coreScale = animatedPressCoreScale,
                smokeBlueTint = animatedPressSmokeBlueTint,
                thinMode = animatedPressBottomRightThinMode
            )
    )
}

private enum class PressQuadrant {
    TopLeft, TopRight, BottomLeft, BottomRight
}
