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
    strokeColor: Color = Color(red = 0.10f, green = 0.30f, blue = 1.00f, alpha = 1f),
    glowColor: Color = Color(red = 0.04f, green = 0.10f, blue = 0.55f, alpha = 1f),
    cornerRadius: Dp = 24.dp,
    initialHaloBorderWidth: Dp = 4.dp,
    pressedHaloBorderWidth: Dp = 4.dp
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedHalo by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 280),
        label = ""
    )
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
    val animatedBlurRadius by animateDpAsState(
        targetValue = if (isPressed) 22.dp else 2.dp,
        animationSpec = tween(durationMillis = 280),
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
                strokeColor = strokeColor,
                glowColor = glowColor,
                cornerRadius = cornerRadius,
                haloWidth = animatedHalo,
                blurRadius = animatedBlurRadius,
                intensity = animatedIntensity,
                press = animatedPress
            )
    )
}

private fun Modifier.simpleAgslRectHaloBorder(
    strokeColor: Color,
    glowColor: Color,
    cornerRadius: Dp,
    haloWidth: Dp,
    blurRadius: Dp,
    intensity: Float,
    press: Float
): Modifier = composed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@composed this

    val density = LocalDensity.current
    val cornerPx = with(density) { cornerRadius.toPx() }
    val haloPx = with(density) { haloWidth.toPx() }
    val blurPx = with(density) { blurRadius.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

//    val runtimeShader = remember { RuntimeShader(SimpleRectHaloAgsl) }
    val runtimeShader = remember { RuntimeShader(SimpleRectHaloAgsl_V2) }
    val effect = remember(
        widthPx,
        heightPx,
        cornerPx,
        haloPx,
        blurPx,
        intensity,
        press,
        strokeColor,
        glowColor
    ) {
        if (widthPx <= 0 || heightPx <= 0) return@remember null
        runCatching {
            runtimeShader.setFloatUniform("uResolution", widthPx.toFloat(), heightPx.toFloat())
            runtimeShader.setFloatUniform("uCornerPx", cornerPx)
            runtimeShader.setFloatUniform("uHaloPx", haloPx.coerceAtLeast(1f))
            runtimeShader.setFloatUniform("uBlurPx", blurPx.coerceAtLeast(1f))
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
uniform float uHaloPx;
uniform float uBlurPx;
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
    float2 center = uResolution * 0.5;
    float2 p = fragCoord - center;

    float haloPx = max(uHaloPx, 1.0);

    // Full-rect shape in pixel space
    float2 halfSize = (uResolution * 0.5) - float2(1.0);
    float radius = clamp(uCornerPx, 0.0, min(halfSize.x, halfSize.y) - 1.0);

    float signedD = sdRoundBox(p, halfSize, radius);

    // Outside-only distance for halo
    float dOut = max(signedD, 0.0);
    float outsideMask = step(0.0, signedD);

    // Blur radius comes from UI: 2dp rest -> 16dp pressed.
    float blurRadius = max(uBlurPx, 1.0);
    float halo = exp(-dOut / blurRadius);

    // Stroke only outside contour. Interior must remain fully transparent.
    float edgeW = max(haloPx * 2.1, 1.0);
    float edge = (1.0 - smoothstep(0.0, edgeW * 0.82, dOut)) * outsideMask;

    // In rest state: no halo, only dark stroke.
    float haloPart = halo * 1.45 * uPress * outsideMask;
    float edgePart = edge * mix(1.08, 1.18, uPress);

    // Combine alpha (no solid "core fill" -> no flat plate look)
    float a = (haloPart + edgePart) * uIntensity * outsideMask;
    a = clamp(a, 0.0, 1.0);

    // In rest state stroke uses dark glow color, then shifts to edge color on press.
    float3 saturatedEdge = float3(
        uEdgeColor.r * 0.50,
        uEdgeColor.g * 0.66,
        min(1.0, uEdgeColor.b * 0.96)
    );
    float3 edgeColor = mix(uGlowColor.rgb, saturatedEdge, 0.32 + 0.58 * uPress);
    float3 softHaloColor = float3(
        uGlowColor.r * 0.92,
        uGlowColor.g * 1.02,
        min(1.0, uGlowColor.b * 1.08)
    );
    float3 rgb = (softHaloColor * haloPart + edgeColor * edgePart) * uIntensity * outsideMask;

    return half4(half3(rgb), half(a * uGlowColor.a));
}
"""
