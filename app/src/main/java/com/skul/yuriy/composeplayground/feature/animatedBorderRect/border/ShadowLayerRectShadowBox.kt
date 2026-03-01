package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import com.skul.yuriy.composeplayground.util.shadowborder.drawOutlineRoundedRectShadowByShadowLayer

@Composable
fun ShadowLayerRectShadowBox(
    modifier: Modifier = Modifier,
    color: Color,
    cornerRadius: Dp,
    initialHaloBorderWidth: Dp,
    pressedHaloBorderWidth: Dp
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedSpread by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    Box(
        modifier = modifier
            .drawOutlineRoundedRectShadowByShadowLayer(
                color = color,
                haloBorderWidth = animatedSpread,
                cornerRadius = cornerRadius
            )
            .then(
                if (isPressed) {
                    Modifier.drawOutlineRoundedRectShadowByShadowLayer(
                        color = color,
                        haloBorderWidth = 4.dp,
                        cornerRadius = cornerRadius
                    )
                } else {
                    Modifier
                }
            )
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) {}
    )
}
