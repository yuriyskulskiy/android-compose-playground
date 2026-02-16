package com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.complex

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

/**
 * Verbose reference variant of local + dynamic Gaussian 17-tap blur.
 *
 * This variant and `alphaGaussianBlurLocalDynamicThreeZone` are functionally equivalent
 * and produce the same visual result.
 * The difference is implementation style:
 * - this one uses two separate AGSL sources (mask + tint).
 * - the optimized variant reuses a single AGSL source for both passes and branches by `uDir`.
 *
 * For pass wiring, the second pass must be called with direction (`uDir = 0,1`)
 * to write tinted output.
 */
fun Modifier.alphaGaussianBlurLocalDynamicThreeZoneLong(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
    topOffset: Dp? = null,
    bottomOffset: Dp? = null,
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val maxRadiusPx = with(density) { radius.toPx() }

    var heightPx by remember { mutableIntStateOf(0) }

    val computedTopPx = with(density) { topOffset?.toPx() } ?: (heightPx * 0.3333333f)
    val computedBottomPx = with(density) { bottomOffset?.toPx() } ?: (heightPx * 0.3333333f)

    val effect = rememberAlphaGaussianBlurLocalDynamicThreeZoneEffectLong(
        maxRadiusPx = maxRadiusPx,
        heightPx = heightPx,
        topPx = computedTopPx,
        bottomPx = computedBottomPx,
        color = color,
    )

    this
        .onSizeChanged { heightPx = it.height }
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
            effect?.let { renderEffect = it.asComposeRenderEffect() }
        }
}

/**
 * Gaussian alpha blur with 3 vertical zones:
 * - Top zone: blur radius decreases from maxRadius -> 0
 * - Middle zone: no blur
 * - Bottom zone: blur radius increases from 0 -> maxRadius
 *
 * Zones:
 * - Default: 33% / clear / 33%
 * - Or fixed by topOffset / bottomOffset (dp)
 */

// ----------------------------- AGSL (Pass 1: Gaussian alpha blur) -----------------------------

private const val AlphaGaussianBlur17TapThreeZoneAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px
uniform float  uTopPx;     // px
uniform float  uBottomPx;  // px

float localRadius(float y) {
    float h = max(1.0, uHeight);
    float top = max(0.0, uTopPx);
    float bottom = max(0.0, uBottomPx);

    float midStart = top;
    float midEnd = max(midStart, h - bottom);

    if (y < midStart) {
        float k = clamp(y / max(1.0, top), 0.0, 1.0);
        return uMaxRadius * (1.0 - k);
    }

    if (y <= midEnd) {
        return 0.0;
    }

    float k = clamp((y - midEnd) / max(1.0, bottom), 0.0, 1.0);
    return uMaxRadius * k;
}

half4 main(float2 p) {
    float r = localRadius(p.y);

    // No blur region
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

// ----------------------------- AGSL (Pass 2: Gaussian alpha blur + tint) -----------------------------

private const val AlphaGaussianBlur17TapThreeZoneTintAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px
uniform float  uTopPx;     // px
uniform float  uBottomPx;  // px
uniform float4 uColor;     // rgba

float localRadius(float y) {
    float h = max(1.0, uHeight);
    float top = max(0.0, uTopPx);
    float bottom = max(0.0, uBottomPx);

    float midStart = top;
    float midEnd = max(midStart, h - bottom);

    if (y < midStart) {
        float k = clamp(y / max(1.0, top), 0.0, 1.0);
        return uMaxRadius * (1.0 - k);
    }

    if (y <= midEnd) {
        return 0.0;
    }

    float k = clamp((y - midEnd) / max(1.0, bottom), 0.0, 1.0);
    return uMaxRadius * k;
}

half4 main(float2 p) {
    float r = localRadius(p.y);

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
private fun rememberAlphaGaussianBlurLocalDynamicThreeZoneEffectLong(
    maxRadiusPx: Float,
    heightPx: Int,
    topPx: Float,
    bottomPx: Float,
    color: Color,
): RenderEffect? {
    if (heightPx <= 0) return null

    val r = max(0f, maxRadiusPx)
    val h = max(1, heightPx).toFloat()

    val tPx = max(0f, topPx)
    val bPx = max(0f, bottomPx)

    return remember(r, h, tPx, bPx, color) {
        val rtH = RuntimeShader(AlphaGaussianBlur17TapThreeZoneAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
            setFloatUniform("uTopPx", tPx)
            setFloatUniform("uBottomPx", bPx)
        }

        val rtV = RuntimeShader(AlphaGaussianBlur17TapThreeZoneTintAglsl).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
            setFloatUniform("uTopPx", tPx)
            setFloatUniform("uBottomPx", bPx)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}
