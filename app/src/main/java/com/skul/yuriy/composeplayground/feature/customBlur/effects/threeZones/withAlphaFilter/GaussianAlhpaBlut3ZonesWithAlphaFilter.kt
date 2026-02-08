package com.skul.yuriy.composeplayground.feature.customBlur.effects.threeZones.withAlphaFilter


import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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

/**
 * Gaussian alpha blur with 3 vertical zones:
 * - Top zone: blur radius decreases from maxRadius -> 0
 * - Middle zone: no blur (fully sharp)
 * - Bottom zone: blur radius increases from 0 -> maxRadius
 *
 * Additionally applies an alpha-threshold inside AGSL (no ColorMatrix), controlled by threshold01 in [0..1].
 *
 * Zones:
 * - Default: 33% / clear / 33%
 * - Or fixed by topOffset / bottomOffset (dp)
 */

// ----------------------------- Public Modifier -----------------------------

fun Modifier.alphaGaussianBlurThreeZoneWithThreshold(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
    topOffset: Dp? = null,
    bottomOffset: Dp? = null,
    threshold01: Float = 0f, // 0..1; 0 disables threshold
    thresholdSoftness01: Float = 0f, // 0..1; 0 = hard step, >0 = smoothstep width
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val maxRadiusPx = with(density) { radius.toPx() }

    var heightPx by remember { mutableIntStateOf(0) }

    val computedTopPx = with(density) { topOffset?.toPx() } ?: (heightPx * 0.3333333f)
    val computedBottomPx = with(density) { bottomOffset?.toPx() } ?: (heightPx * 0.3333333f)

    val effect = rememberAlphaGaussianBlurThreeZoneEffect(
        maxRadiusPx = maxRadiusPx,
        heightPx = heightPx,
        topPx = computedTopPx,
        bottomPx = computedBottomPx,
        color = color,
        threshold01 = threshold01,
        thresholdSoftness01 = thresholdSoftness01,
    )

    this
        .onSizeChanged { heightPx = it.height }
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
            // Optional, but helps against edge artifacts on some GPUs:
            // clip = true
            effect?.let { renderEffect = it.asComposeRenderEffect() }
        }
}

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

// ----------------------------- AGSL (Pass 2: Gaussian alpha blur + tint + threshold) -----------------------------

private const val AlphaGaussianBlur17TapThreeZoneTintThresholdAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px
uniform float  uTopPx;     // px
uniform float  uBottomPx;  // px
uniform float4 uColor;     // rgba

uniform float  uThreshold01;        // 0..1
uniform float  uThresholdSoft01;    // 0..1 (0 = hard step)

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

float applyThreshold(float a01) {
    float thr = clamp(uThreshold01, 0.0, 1.0);

    // Threshold disabled
    if (thr <= 0.0) return a01;

    float soft = clamp(uThresholdSoft01, 0.0, 1.0);

    // Hard threshold
    if (soft <= 0.0) {
        return step(thr, a01);
    }

    // Soft threshold (smooth edge)
    // Interpret softness as width around the threshold in alpha-space
    float w = soft * 0.25; // keep it reasonable; 0.25 is a good practical cap
    return smoothstep(thr - w, thr + w, a01);
}

half4 main(float2 p) {
    float r = localRadius(p.y);

    if (r < 0.5) {
        // Keep the source alpha (already 0..1 in shader space)
        float a01 = src.eval(p).a;
        a01 = applyThreshold(a01);
        half a = half(a01) * half(uColor.a);
        return half4(uColor.rgb, a);
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

    float a01 = (sumW > 0.0) ? (sum / sumW) : 0.0;
    a01 = applyThreshold(a01);

    half a = half(a01) * half(uColor.a);
    return half4(uColor.rgb, a);
}
"""

// ----------------------------- RenderEffect builder -----------------------------

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaGaussianBlurThreeZoneEffect(
    maxRadiusPx: Float,
    heightPx: Int,
    topPx: Float,
    bottomPx: Float,
    color: Color,
    threshold01: Float,
    thresholdSoftness01: Float,
): RenderEffect? {
    if (heightPx <= 0) return null

    val r = max(0f, maxRadiusPx)
    val h = max(1, heightPx).toFloat()

    val tPx = max(0f, topPx)
    val bPx = max(0f, bottomPx)

    val thr = threshold01.coerceIn(0f, 1f)
    val soft = thresholdSoftness01.coerceIn(0f, 1f)

    return remember(r, h, tPx, bPx, color, thr, soft) {
        val rtH = RuntimeShader(AlphaGaussianBlur17TapThreeZoneAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
            setFloatUniform("uTopPx", tPx)
            setFloatUniform("uBottomPx", bPx)
        }

        val rtV = RuntimeShader(AlphaGaussianBlur17TapThreeZoneTintThresholdAglsl).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
            setFloatUniform("uTopPx", tPx)
            setFloatUniform("uBottomPx", bPx)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
            setFloatUniform("uThreshold01", thr)
            setFloatUniform("uThresholdSoft01", soft)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}
