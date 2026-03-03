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
        color = color,
        cornerRadius = cornerRadius,
        initialHaloBorderWidth = initialHaloBorderWidth,
        pressedHaloBorderWidth = pressedHaloBorderWidth,
        isPressed = isPressed
    )
}

@Composable
private fun RadialLinearDraftRectShadowBoxContent(
    modifier: Modifier,
    color: Color,
    cornerRadius: Dp,
    initialHaloBorderWidth: Dp,
    pressedHaloBorderWidth: Dp,
    isPressed: Boolean
) {
    val animatedSpread by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 300),
        label = "haloSpread"
    )

    Box(
        modifier = modifier
            .drawOutlineRoundedRectShadowGradientDraft(
                color = color.copy(alpha = 0.6f),
                haloBorderWidth = animatedSpread,
                cornerRadius = cornerRadius
            )
            .then(
                if (isPressed) {
                    Modifier.drawOutlineRoundedRectShadowGradientDraft(
                        color = color,
                        haloBorderWidth = 4.dp,
                        cornerRadius = cornerRadius
                    )
                } else {
                    Modifier
                }
            )
    )
}
