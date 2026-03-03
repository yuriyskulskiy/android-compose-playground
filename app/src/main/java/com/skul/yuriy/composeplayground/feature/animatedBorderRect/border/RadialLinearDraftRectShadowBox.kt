package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RadialLinearDraftRectShadowBox(
    modifier: Modifier = Modifier,
    color: Color,
    cornerRadius: Dp,
    initialHaloBorderWidth: Dp,
    pressedHaloBorderWidth: Dp,
    onClick: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    val onClickState = rememberUpdatedState(onClick)
    val animatedSpread by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 300),
        label = "haloSpread"
    )
    val haloColor = color.copy(alpha = 0.6f)
    val isHaloExpanded = animatedSpread > initialHaloBorderWidth
    /**
     * Performance optimization for tap spam:
     * we keep the stroke bright while halo is still above its initial width.
     * Without this guard, rapid press/release toggles would flip stroke alpha
     * between idle and active states every gesture frame, causing extra stroke
     * recomposition/invalidation and redundant redraw churn.
     */
    val strokeColor = color.copy(alpha = if (isPressed || isHaloExpanded) 1f else 0.5f)

    RadialLinearDraftRectShadowBoxContent(
        modifier = modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                try {
                    val up = waitForUpOrCancellation()
                    if (up != null) onClickState.value()
                } finally {
                    isPressed = false
                }
            }
        },
        haloColor = haloColor,
        strokeColor = strokeColor,
        cornerRadius = cornerRadius,
        haloBorderWidth = animatedSpread
    )
}

@Composable
private fun RadialLinearDraftRectShadowBoxContent(
    modifier: Modifier,
    haloColor: Color,
    strokeColor: Color,
    cornerRadius: Dp,
    haloBorderWidth: Dp
) {
    Box(
        modifier = modifier
            .drawOutlineRoundedRectShadowGradientDraft(
                color = haloColor,
                haloBorderWidth = haloBorderWidth,
                cornerRadius = cornerRadius
            )
            .drawOutlineRoundedRectShadowGradientDraft(
                color = strokeColor,
                haloBorderWidth = 4.dp,
                cornerRadius = cornerRadius
            )
    )
}
