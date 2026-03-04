package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurrender

import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Demonstrates a saturated outer glow built with `graphicsLayer` scale + `RenderEffect` blur.
 *
 * Problem background:
 * - Blurring content inside the same composable bounds often produces a weak, transparent-looking
 *   shadow rather than a dense glow.
 * - A common workaround is to move blur into an outer wrapper composable that is larger than the
 *   content container. This gives blur room to spread beyond the visible content bounds.
 *
 * Why the common wrapper approach is painful:
 * - If the outer blur container grows during press animation, section height can appear to expand
 *   inside a `Column`.
 * - To hide that layout growth, developers often add one more fixed-size wrapper around it.
 * - The result is split logic across multiple nested containers and harder-to-maintain UI structure.
 *
 * Approach used here:
 * - Keep layout size stable.
 * - Draw glow and apply blur in the same layer.
 * - Increase perceived halo by scaling only the glow layer (`graphicsLayer.scaleX/scaleY`), so
 *   pixels can extend outside container bounds without changing measured layout size.
 *
 * Important limitation:
 * - `graphicsLayer` effects are applied to the whole layer output.
 * - Because of that, this layer alone cannot produce a "blur only outside, fully transparent inside"
 *   cutout result.
 * - To preserve sharp content and avoid blur on inner pixels, this implementation uses a second
 *   top layer (`StaticContentLayer`) that clips and paints content over the glow.
 *
 * Modifier-order nuance used in this demo:
 * - The thin debug rim (`drawOutlineOutsideStroke`) is intentionally placed before the glow
 *   layer's `graphicsLayer` call in the modifier chain.
 * - This keeps the rim visible as an external reference contour, because it should not be scaled
 *   and blurred together with the glow body.
 */
@Composable
fun RenderEffectBlurRectShadowBox(
    modifier: Modifier = Modifier,
    color: Color,
    cornerRadius: Dp,
    initialBlurRadius: Dp,
    pressedBlurRadius: Dp,
    initialHaloShadowWidth: Dp,
    pressedHaloShadowWidth: Dp
) {
    var isPressed by remember { mutableStateOf(false) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .onSizeChanged { contentSize = it }
    ) {
        AnimatedBlurLayer(
            modifier = Modifier.fillMaxSize(),
            color = color,
            cornerRadius = cornerRadius,
            contentSize = contentSize,
            isPressed = isPressed,
            initialBlurRadius = initialBlurRadius,
            pressedBlurRadius = pressedBlurRadius,
            initialHaloShadowWidth = initialHaloShadowWidth,
            pressedHaloShadowWidth = pressedHaloShadowWidth
        )

        StaticContentLayer(
            modifier = Modifier.fillMaxSize(),
            cornerRadius = cornerRadius,
            onPressedChanged = { isPressed = it }
        )
    }
}

@Composable
private fun AnimatedBlurLayer(
    modifier: Modifier,
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp = 2.dp,
    contentSize: IntSize,
    isPressed: Boolean,
    initialBlurRadius: Dp,
    pressedBlurRadius: Dp,
    initialHaloShadowWidth: Dp,
    pressedHaloShadowWidth: Dp
) {
    val animatedBlurRadius by animateDpAsState(
        targetValue = if (isPressed) pressedBlurRadius else initialBlurRadius,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    val animatedHaloBorderSize by animateDpAsState(
        targetValue = if (isPressed) pressedHaloShadowWidth else initialHaloShadowWidth,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    val density = LocalDensity.current
    val haloPx = with(density) { animatedHaloBorderSize.toPx() }
    val blurPx = with(density) { animatedBlurRadius.toPx().coerceAtLeast(0.01f) }

    val widthPx = contentSize.width.toFloat().coerceAtLeast(1f)
    val heightPx = contentSize.height.toFloat().coerceAtLeast(1f)
    // Same halo thickness on all sides: +2*halo in size, axis-specific scale factors.
    val glowScaleX = 1f + (haloPx * 2f / widthPx)
    val glowScaleY = 1f + (haloPx * 2f / heightPx)

    val glowRenderEffect = remember(blurPx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AndroidRenderEffect
                .createBlurEffect(blurPx, blurPx, Shader.TileMode.DECAL)
                .asComposeRenderEffect()
        } else {
            null
        }
    }

    Box(
        modifier = modifier
            .drawOutlineOutsideStroke(
                color = color.copy(alpha = if (isPressed) 0.8f else 0.2f),
                outsideWidth = strokeWidth, // debug
                cornerRadius = cornerRadius
            )
            .graphicsLayer {
                scaleX = glowScaleX
                scaleY = glowScaleY
                transformOrigin = TransformOrigin.Center
                renderEffect = glowRenderEffect
            }
            .drawOutlineRoundedRectFill(
                color = color.copy(alpha = 0.6f),
                cornerRadius = cornerRadius
            )
    )
}

@Composable
private fun StaticContentLayer(
    modifier: Modifier,
    cornerRadius: Dp,
    onPressedChanged: (Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                shape = RoundedCornerShape(cornerRadius)
                clip = true
            }
            .background(color = Color.Black)
//            .drawOutlineRectStroke(
//                color = if (isPressed) color else color.copy(alpha = 0.95f),
//                strokeWidth = if (isPressed) 4.dp else 2.dp,
//                cornerRadius = cornerRadius
//            )
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    onPressedChanged(true)
                    waitForUpOrCancellation()
                    onPressedChanged(false)
                }
            }
    )
}
