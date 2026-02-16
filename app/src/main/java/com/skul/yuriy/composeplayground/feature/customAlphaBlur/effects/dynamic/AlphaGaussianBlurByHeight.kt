package com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.dynamic

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max


// ----------------------------- Public Modifier -----------------------------

fun Modifier.alphaGaussianBlurByHeight(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val maxRadiusPx = with(density) { radius.toPx() }
    val thresholdPxFor61Tap = with(density) { 16.dp.toPx() }

    var heightPx by remember { mutableIntStateOf(0) }

    val effect = rememberAlphaGaussianBlurByHeightEffect(
        maxRadiusPx = maxRadiusPx,
        heightPx = heightPx,
        color = color,
        thresholdPxFor61Tap = thresholdPxFor61Tap,
    )

    this
        .onSizeChanged { heightPx = it.height }
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
            renderEffect = effect.asComposeRenderEffect()
        }
}

// ----------------------------- AGSL (Pass 1: Gaussian alpha blur by height) -----------------------------

private const val AlphaGaussianBlur17TapByHeightAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px

half4 main(float2 p) {
    float h = max(1.0, uHeight);
    float t = clamp(p.y / h, 0.0, 1.0);
    float r = uMaxRadius * t;

    // No blur at the top (or very small radius)
    if (r < 0.5) {
        half a0 = src.eval(p).a;
        return half4(0.0, 0.0, 0.0, a0);
    }

    float sigma = max(0.001, r * 0.57735);
    float stepPx = max(0.25, r / 8.0);

    float sum = 0.0;
    float sumW = 0.0;

    for (int i = -8; i <= 8; i++) {
        float x = float(i) * stepPx;
        float w = exp(-(x * x) / (2.0 * sigma * sigma));
        sum += src.eval(p + uDir * x).a * w;
        sumW += w;
    }

    half a = (sumW > 0.0) ? half(sum / sumW) : half(0.0);
    return half4(0.0, 0.0, 0.0, a);
}
"""

// ----------------------------- AGSL (Pass 2: Gaussian alpha blur + tint by height) -----------------------------

private const val AlphaGaussianBlur17TapByHeightTintAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px
uniform float4 uColor;     // rgba

half4 main(float2 p) {
    float h = max(1.0, uHeight);
    float t = clamp(p.y / h, 0.0, 1.0);
    float r = uMaxRadius * t;

    if (r < 0.5) {
        half a0 = src.eval(p).a * half(uColor.a);
        return half4(uColor.rgb, a0);
    }

    float sigma = max(0.001, r * 0.57735);
    float stepPx = max(0.25, r / 8.0);

    float sum = 0.0;
    float sumW = 0.0;

    for (int i = -8; i <= 8; i++) {
        float x = float(i) * stepPx;
        float w = exp(-(x * x) / (2.0 * sigma * sigma));
        sum += src.eval(p + uDir * x).a * w;
        sumW += w;
    }

    half a = (sumW > 0.0) ? half(sum / sumW) * half(uColor.a) : half(0.0);
    return half4(uColor.rgb, a);
}
"""

// ----------------------------- AGSL (61 taps variant for large radii) -----------------------------

private const val AlphaGaussianBlur61TapByHeightAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px

half4 main(float2 p) {
    float h = max(1.0, uHeight);
    float t = clamp(p.y / h, 0.0, 1.0);
    float r = uMaxRadius * t;

    if (r < 0.5) {
        half a0 = src.eval(p).a;
        return half4(0.0, 0.0, 0.0, a0);
    }

    float sigma = max(0.001, r * 0.57735);
    float stepPx = max(0.125, r / 30.0);

    float sum = 0.0;
    float sumW = 0.0;

    for (int i = -30; i <= 30; i++) {
        float x = float(i) * stepPx;
        float w = exp(-(x * x) / (2.0 * sigma * sigma));
        sum += src.eval(p + uDir * x).a * w;
        sumW += w;
    }

    half a = (sumW > 0.0) ? half(sum / sumW) : half(0.0);
    return half4(0.0, 0.0, 0.0, a);
}
"""

private const val AlphaGaussianBlur61TapByHeightTintAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px
uniform float4 uColor;     // rgba

half4 main(float2 p) {
    float h = max(1.0, uHeight);
    float t = clamp(p.y / h, 0.0, 1.0);
    float r = uMaxRadius * t;

    if (r < 0.5) {
        half a0 = src.eval(p).a * half(uColor.a);
        return half4(uColor.rgb, a0);
    }

    float sigma = max(0.001, r * 0.57735);
    float stepPx = max(0.125, r / 30.0);

    float sum = 0.0;
    float sumW = 0.0;

    for (int i = -30; i <= 30; i++) {
        float x = float(i) * stepPx;
        float w = exp(-(x * x) / (2.0 * sigma * sigma));
        sum += src.eval(p + uDir * x).a * w;
        sumW += w;
    }

    half a = (sumW > 0.0) ? half(sum / sumW) * half(uColor.a) : half(0.0);
    return half4(uColor.rgb, a);
}
"""

// ----------------------------- RenderEffect builder -----------------------------

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaGaussianBlurByHeightEffect(
    maxRadiusPx: Float,
    heightPx: Int,
    color: Color,
    thresholdPxFor61Tap: Float,
): RenderEffect {
    val r = max(0f, maxRadiusPx)
    val h = max(1, heightPx).toFloat()
    val use61TapKernel = r > thresholdPxFor61Tap

    val horizontalShader = remember(use61TapKernel) {
        RuntimeShader(
            if (use61TapKernel) {
                AlphaGaussianBlur61TapByHeightAglsl
            } else {
                AlphaGaussianBlur17TapByHeightAglsl
            }
        )
    }

    val verticalShader = remember(use61TapKernel) {
        RuntimeShader(
            if (use61TapKernel) {
                AlphaGaussianBlur61TapByHeightTintAglsl
            } else {
                AlphaGaussianBlur17TapByHeightTintAglsl
            }
        )
    }

    return remember(r, h, color, use61TapKernel, horizontalShader, verticalShader) {
        val rtH = horizontalShader.apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
        }

        val rtV = verticalShader.apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}
