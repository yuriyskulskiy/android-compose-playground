package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.png

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val HaloPngAspectRatio = 1792f / 1024f
private const val HaloPngWidthScale = 0.91f
private const val HaloPngHeightScale = 1.015f
private val HaloExtraWidth = 72.dp

@Composable
internal fun HaloPngGlowingBox(
    contentWidth: Dp,
    contentHeight: Dp,
    cornerRadius: Dp,
    idleResId: Int,
    activeResId: Int,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    val haloBaseWidth = contentWidth + HaloExtraWidth
    val haloWidth = haloBaseWidth * HaloPngWidthScale
    val haloHeight = (haloBaseWidth / HaloPngAspectRatio) * HaloPngHeightScale
    val haloLayerModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                waitForUpOrCancellation()
                isPressed = false
            }
        }
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
        .drawWithContent {
            drawContent()

            val cutoutWidth = contentWidth.toPx()
            val cutoutHeight = contentHeight.toPx()
            val left = (size.width - cutoutWidth) / 2f
            val top = (size.height - cutoutHeight) / 2f
            val corner = cornerRadius.toPx()

            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(cutoutWidth, cutoutHeight),
                cornerRadius = CornerRadius(corner, corner),
                blendMode = BlendMode.Clear
            )
        }

    Box(
        modifier = modifier
            .width(haloWidth)
            .height(haloHeight),
        contentAlignment = contentAlignment
    ) {
        Box(
            modifier = haloLayerModifier
        ) {
            Crossfade(
                targetState = isPressed,
                modifier = Modifier.fillMaxSize(),
                label = "pngBorderState"
            ) { pressed ->
                Image(
                    painter = painterResource(
                        if (pressed) activeResId else idleResId
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Box(
            modifier = Modifier
                .width(contentWidth)
                .height(contentHeight),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}
