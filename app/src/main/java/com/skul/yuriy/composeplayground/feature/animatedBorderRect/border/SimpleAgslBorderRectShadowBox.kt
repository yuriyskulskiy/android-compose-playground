package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.animateDpAsState
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

@Composable
fun SimpleAgslBorderRectShadowBox(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(red = 0.10f, green = 0.30f, blue = 1.00f, alpha = 1f),
    cornerRadius: Dp = 24.dp,
    initialHaloBorderWidth: Dp = 0.dp,
    pressedHaloBorderWidth: Dp = 36.dp
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedThinWidth = 4.dp
    val animatedIntensity by animateFloatAsState(
        targetValue = if (isPressed) 1.6f else 1f,
        animationSpec = tween(durationMillis = 280),
        label = ""
    )
    val animatedPress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 280),
        label = ""
    )
    val animatedPressGlowWidth by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 280),
        label = ""
    )
    val animatedPressGlowBlur by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 280),
        label = ""
    )
    val strokeColor = baseColor
    val glowColor = Color(
        red = (baseColor.red * 0.40f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.33f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 0.55f).coerceIn(0f, 1f),
        alpha = baseColor.alpha
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
                strokeColor = strokeColor,
                glowColor = glowColor,
                cornerRadius = cornerRadius,
                thinWidth = animatedThinWidth,
                thinBlur = 2.dp,
                pressGlowWidth = animatedPressGlowWidth,
                pressGlowBlur = animatedPressGlowBlur,
                intensity = animatedIntensity,
                press = animatedPress
            )
    )
}

private fun Modifier.simpleAgslRectHaloBorder(
    strokeColor: Color,
    glowColor: Color,
    cornerRadius: Dp,
    thinWidth: Dp,
    thinBlur: Dp,
    pressGlowWidth: Dp,
    pressGlowBlur: Dp,
    intensity: Float,
    press: Float
): Modifier = composed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@composed this

    val density = LocalDensity.current
    val cornerPx = with(density) { cornerRadius.toPx() }
    val thinWidthPx = with(density) { thinWidth.toPx() }
    val thinBlurPx = with(density) { thinBlur.toPx() }
    val pressGlowWidthPx = with(density) { pressGlowWidth.toPx() }
    val pressGlowBlurPx = with(density) { pressGlowBlur.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

//    val runtimeShader = remember { RuntimeShader(SimpleRectHaloAgsl) }
    val runtimeShader = remember { RuntimeShader(SimpleRectHaloAgsl_V2) }
    val effect = remember(
        widthPx,
        heightPx,
        cornerPx,
        thinWidthPx,
        thinBlurPx,
        pressGlowWidthPx,
        pressGlowBlurPx,
        intensity,
        press,
        strokeColor,
        glowColor
    ) {
        if (widthPx <= 0 || heightPx <= 0) return@remember null
        runCatching {
            runtimeShader.setFloatUniform("uResolution", widthPx.toFloat(), heightPx.toFloat())
            runtimeShader.setFloatUniform("uCornerPx", cornerPx)
            runtimeShader.setFloatUniform("uThinWidthPx", thinWidthPx.coerceAtLeast(1f))
            runtimeShader.setFloatUniform("uThinBlurPx", thinBlurPx.coerceAtLeast(0.001f))
            runtimeShader.setFloatUniform("uPressGlowWidthPx", pressGlowWidthPx.coerceAtLeast(0f))
            runtimeShader.setFloatUniform("uPressGlowBlurPx", pressGlowBlurPx.coerceAtLeast(0.001f))
            runtimeShader.setFloatUniform("uPress", press)
            runtimeShader.setFloatUniform("uIntensity", intensity)
            runtimeShader.setFloatUniform(
                "uEdgeColor",
                strokeColor.red,
                strokeColor.green,
                strokeColor.blue,
                strokeColor.alpha
            )
            runtimeShader.setFloatUniform(
                "uGlowColor",
                glowColor.red,
                glowColor.green,
                glowColor.blue,
                glowColor.alpha
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
uniform float uThinWidthPx;
uniform float uThinBlurPx;
uniform float uPressGlowWidthPx;
uniform float uPressGlowBlurPx;
uniform float uPress;
uniform float uIntensity;

// Base glow color (used for the wide halo)
uniform float4 uGlowColor;

// Thin outline color (used for the crisp edge)
uniform float4 uEdgeColor;

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

    // TEST MODE:
    // Only red radial-by-distance alpha around contour:
    // alpha at border = 0.6, fades to 0 at width distance.
    float maxDistPx = max(uPressGlowWidthPx, 0.001);
    float t = clamp(dOut / maxDistPx, 0.0, 1.0);
    float widthMask = step(0.001, uPressGlowWidthPx);
    float band = (1.0 - t) * outsideMask * widthMask;

    // Keep all uniforms "used" so runtime uniform updates stay stable in test mode.
    float keep = (
        uThinWidthPx + uThinBlurPx + uPressGlowWidthPx + uPressGlowBlurPx +
        uPress + uIntensity + uGlowColor.r + uGlowColor.g + uGlowColor.b + uGlowColor.a
    ) * 0.0;

    float a = clamp(0.6 * band + keep, 0.0, 1.0);
    float3 rgb = float3(1.0, 0.0, 0.0) * a;
    return half4(half3(rgb), half(a)) + base * 0.0;
}
"""
