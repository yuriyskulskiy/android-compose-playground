package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.util.shadowborder.drawOutlineRoundedRectShadowByShadowLayer

@Composable
fun ShadowLayerRectShadowBox(
    modifier: Modifier = Modifier,
    color: Color,
    cornerRadius: Dp,
    initialHaloBorderWidth: Dp,
    pressedHaloBorderWidth: Dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedSpread by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    Box(
        modifier = modifier
            .drawOutlineRoundedRectShadowByShadowLayer(
                color = color.copy(alpha = 0.6f),
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
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {}
    )
}
