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

enum class SimpleAgslRenderMode {
    /**
     * AGSL is applied via [android.graphics.RenderEffect.createRuntimeShaderEffect].
     */
    RenderEffect,

    /**
     * AGSL is attached directly to native [android.graphics.Paint.shader] and drawn on canvas.
     */
    CanvasPaint
}

/**
 * Simple AGSL rectangular halo border (single-shader implementation).
 *
 * This box supports two rendering backends controlled by [renderMode]:
 * 1) [SimpleAgslRenderMode.RenderEffect]
 * - Uses RuntimeShader + RenderEffect.
 * - In this demo, RenderEffect is recreated when parameters change (this keeps behavior explicit
 *   and predictable while tuning visuals).
 * - Practical note: this mode is usually preferred when you want a pure post-effect pipeline and
 *   easy composition with other effects.
 *
 * 2) [SimpleAgslRenderMode.CanvasPaint]
 * - Uses RuntimeShader attached to native Paint and drawn via canvas.
 * - No RenderEffect object recreation is needed in this mode.
 * - Native Paint is cached with `remember` to avoid reallocation.
 * - In this screen, a practical workaround is applied: draw rect is expanded by negative offsets
 *   (`-overflow`) so halo is not visually cut by the shape contour.
 *
 * Important visual/perf notes for the article:
 * - RenderEffect path can more naturally produce a halo that visually extends beyond the inner
 *   rounded contour (subject to parent clipping rules).
 * - CanvasPaint path is reflected in HWUI as extra canvas draw work (blue bars), since rendering
 *   goes through explicit canvas draw calls.
 * - The border is optimized as a single AGSL script: there is no separate stroke modifier; both
 *   the thin contour and the halo falloff are generated inside the shader.
 *
 * External inputs remain minimal:
 * - [color]: single tint for both idle and pressed states.
 * - [maxHaloBorderWidth]: max outer halo reach in pressed state.
 *
 * Internal profile:
 * - Idle: `0..2dp = 0.5 alpha`, `2..8dp -> 0`.
 * - Pressed: `0..2dp = 1`, `2..6dp -> 0.5`, `6..maxHaloBorderWidth -> 0`.
 */
@Composable
fun SimpleAgslBorderRectShadowBox(
    modifier: Modifier = Modifier,
    color: Color = Color(red = 0.10f, green = 0.30f, blue = 1.00f, alpha = 1f),
    cornerRadius: Dp = 24.dp,
    maxHaloBorderWidth: Dp = 32.dp,
    renderMode: SimpleAgslRenderMode = SimpleAgslRenderMode.RenderEffect
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
                press = animatedPress,
                renderMode = renderMode
            )
    )
}
