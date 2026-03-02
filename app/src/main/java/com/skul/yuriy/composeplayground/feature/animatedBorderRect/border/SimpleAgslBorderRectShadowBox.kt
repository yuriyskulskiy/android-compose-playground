package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
            .simpleAgslRectHaloBorder(
                color = color,
                cornerRadius = 24.dp,
                maxHaloBorderWidth = maxHaloBorderWidth,
                intensity = animatedIntensity,
                press = animatedPress
            )
    )
}

private fun Modifier.simpleAgslRectHaloBorder(
    color: Color,
    cornerRadius: Dp,
    maxHaloBorderWidth: Dp,
    intensity: Float,
    press: Float
): Modifier = composed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@composed this

    val density = LocalDensity.current
    val cornerPx = with(density) { cornerRadius.toPx() }
    val strokeWidthPx = with(density) { 2.dp.toPx() }
    val idleFadeEndPx = with(density) { 8.dp.toPx() }
    val maxHaloBorderWidthPx = with(density) { maxHaloBorderWidth.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

    val runtimeShader = remember { RuntimeShader(SimpleRectHaloAgsl_V2) }
    val effect = remember(
        widthPx,
        heightPx,
        cornerPx,
        strokeWidthPx,
        idleFadeEndPx,
        maxHaloBorderWidthPx,
        intensity,
        press,
        color
    ) {
        if (widthPx <= 0 || heightPx <= 0) return@remember null
        runCatching {
            runtimeShader.setFloatUniform("uResolution", widthPx.toFloat(), heightPx.toFloat())
            runtimeShader.setFloatUniform("uCornerPx", cornerPx)
            runtimeShader.setFloatUniform("uStrokeWidthPx", strokeWidthPx.coerceAtLeast(0.001f))
            runtimeShader.setFloatUniform("uIdleFadeEndPx", idleFadeEndPx.coerceAtLeast(0.001f))
            runtimeShader.setFloatUniform("uMaxHaloBorderWidthPx", maxHaloBorderWidthPx.coerceAtLeast(0.001f))
            runtimeShader.setFloatUniform("uPress", press)
            runtimeShader.setFloatUniform("uIntensity", intensity)
            runtimeShader.setFloatUniform(
                "uColor",
                color.red,
                color.green,
                color.blue,
                color.alpha
            )

            RenderEffect.createRuntimeShaderEffect(runtimeShader, "src").asComposeRenderEffect()
        }.getOrNull()
    }

    this
        .onSizeChanged {
            widthPx = it.width
            heightPx = it.height
        }
        .then(
            if (widthPx > 0 && heightPx > 0) {
                Modifier.graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    effect?.let { renderEffect = it }
                }
            } else {
                Modifier
            }
        )
        .drawBehind {
            drawRect(color = Color.White)
        }
}

private const val SimpleRectHaloAgsl_V2 = """
uniform shader src;
uniform float2 uResolution;
uniform float uCornerPx;
uniform float uStrokeWidthPx;
uniform float uIdleFadeEndPx;
uniform float uMaxHaloBorderWidthPx;
uniform float uPress;
uniform float uIntensity;
uniform float4 uColor;

float sdRoundBox(float2 p, float2 b, float r) {
    float2 q = abs(p) - b + float2(r);
    return length(max(q, float2(0.0))) + min(max(q.x, q.y), 0.0) - r;
}

half4 main(float2 fragCoord) {
    half4 base = src.eval(fragCoord);

    float2 center = uResolution * 0.5;
    float2 p = fragCoord - center;

    // Full-rect shape in pixel space
    float2 halfSize = (uResolution * 0.5) - float2(1.0);
    float radius = clamp(uCornerPx, 0.0, min(halfSize.x, halfSize.y) - 1.0);

    float signedD = sdRoundBox(p, halfSize, radius);

    // Outside-only distance
    float dOut = max(signedD, 0.0);
    float outsideMask = step(0.0, signedD);

    float d = max(dOut, 0.0);
    float coreW = max(uStrokeWidthPx, 0.001);
    float idleFadeEnd = max(uIdleFadeEndPx, coreW + 0.001);
    float pressSolidEnd = coreW;
    float pressDropEnd = coreW * 3.0;
    float pressTailStart = pressDropEnd;
    float pressTailEnd = max(uMaxHaloBorderWidthPx, pressTailStart + 0.001);

    float idleBand = 0.0;
    if (d <= coreW) {
        idleBand = 0.5;
    } else if (d <= idleFadeEnd) {
        float t = (d - coreW) / max(idleFadeEnd - coreW, 0.001);
        idleBand = mix(0.5, 0.0, t);
    } else {
        idleBand = 0.0;
    }

    float pressBand = 0.0;
    if (d <= pressSolidEnd) {
        pressBand = 1.0;
    } else if (d <= pressDropEnd) {
        float t = (d - pressSolidEnd) / max(pressDropEnd - pressSolidEnd, 0.001);
        pressBand = mix(1.0, 0.5, t);
    } else {
        float t = clamp((d - pressTailStart) / max(pressTailEnd - pressTailStart, 0.001), 0.0, 1.0);
        pressBand = mix(0.5, 0.0, t);
    }

    float band = mix(idleBand, pressBand, clamp(uPress, 0.0, 1.0)) * outsideMask;
    float a = clamp(band, 0.0, 1.0);
    float3 rgb = uColor.rgb * (a * uIntensity);
    return half4(half3(rgb), half(a * uColor.a)) + base * 0.0;
}
"""
