package com.skul.yuriy.composeplayground.feature.customBlur.effects.byHeight

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
import kotlin.math.max


// ----------------------------- Public Modifier -----------------------------

fun Modifier.alphaGaussianBlurByHeight(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val maxRadiusPx = with(density) { radius.toPx() }

    var heightPx by remember { mutableIntStateOf(0) }

    val effect = rememberAlphaGaussianBlurByHeightEffect(
        maxRadiusPx = maxRadiusPx,
        heightPx = heightPx,
        color = color
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

// ----------------------------- RenderEffect builder -----------------------------

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaGaussianBlurByHeightEffect(
    maxRadiusPx: Float,
    heightPx: Int,
    color: Color
): RenderEffect {
    val r = max(0f, maxRadiusPx)
    val h = max(1, heightPx).toFloat()

    return remember(r, h, color) {
        val rtH = RuntimeShader(AlphaGaussianBlur17TapByHeightAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
        }

        val rtV = RuntimeShader(AlphaGaussianBlur17TapByHeightTintAglsl).apply {
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