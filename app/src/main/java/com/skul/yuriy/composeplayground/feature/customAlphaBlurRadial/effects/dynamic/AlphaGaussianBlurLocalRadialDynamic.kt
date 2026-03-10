package com.skul.yuriy.composeplayground.feature.customAlphaBlurRadial.effects.dynamic

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * Custom AGSL-based render effect with variable-radius alpha blur in a local radial region.
 *
 * The blur is evaluated inside the inscribed circle of the target composable
 * (`outerRadius = min(width, height) / 2`) and disabled outside it.
 *
 * Radius changes dynamically by distance from center:
 * - `centerZoneHasMaxBlur = true`: blur is strongest in the center zone and fades to zero toward
 *   the outer circle.
 * - `centerZoneHasMaxBlur = false`: center zone stays unblurred, then blur grows toward the outer
 *   circle.
 *
 * This effect is implemented as a two-pass separable Gaussian blur (horizontal + vertical) with
 * 61 taps per pass.
 */
fun Modifier.alphaGaussianBlurLocalRadialDynamic(
    radius: Dp = 16.dp,
    clearCenterRadius: Dp = 28.dp,
    centerZoneHasMaxBlur: Boolean = true,
    color: Color = Color.Black.copy(alpha = 0.99f),
): Modifier = composed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@composed this

    val density = LocalDensity.current
    val maxRadiusPx = with(density) { radius.coerceAtMost(12.dp).toPx() }
    val clearCenterRadiusPx = with(density) { clearCenterRadius.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

    val effect = rememberAlphaGaussianBlurLocalRadialDynamicEffect(
        maxRadiusPx = maxRadiusPx,
        clearCenterRadiusPx = clearCenterRadiusPx,
        centerZoneHasMaxBlur = centerZoneHasMaxBlur,
        widthPx = widthPx,
        heightPx = heightPx,
        color = color
    )

    this
        .onSizeChanged {
            widthPx = it.width
            heightPx = it.height
        }
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
            effect?.let { renderEffect = it.asComposeRenderEffect() }
        }
}

private const val AlphaGaussianBlur61TapLocalRadialDynamicAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius;         // px
uniform float  uWidth;             // px
uniform float  uHeight;            // px
uniform float  uMinSide;           // px
uniform float  uClearCenterRadius; // px
uniform float  uCenterMaxBlur;     // 1.0 = center max, 0.0 = center clear
uniform float4 uColor;             // rgba

float localRadius(float2 p) {
    float minSide = max(1.0, uMinSide);
    float outer = minSide * 0.5;
    float inner = max(0.0, uClearCenterRadius);
    float2 center = float2(uWidth * 0.5, uHeight * 0.5);
    float dist = length(p - center);

    if (dist >= outer) {
        return 0.0;
    }

    if (uCenterMaxBlur > 0.5) {
        if (dist <= inner) {
            return uMaxRadius;
        }
        float tCenter = clamp((dist - inner) / max(0.001, outer - inner), 0.0, 1.0);
        return uMaxRadius * (1.0 - tCenter);
    }

    if (dist <= inner) {
        return 0.0;
    }
    float tEdge = clamp((dist - inner) / max(0.001, outer - inner), 0.0, 1.0);
    return uMaxRadius * tEdge;
}

half4 main(float2 p) {
    float r = localRadius(p);

    if (r < 0.5) {
        half a0 = src.eval(p).a;
        if (uDir.x > uDir.y) {
            return half4(0.0, 0.0, 0.0, a0);
        }
        return half4(uColor.rgb, a0 * half(uColor.a));
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
    if (uDir.x > uDir.y) {
        return half4(0.0, 0.0, 0.0, a);
    }
    return half4(uColor.rgb, a * half(uColor.a));
}
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaGaussianBlurLocalRadialDynamicEffect(
    maxRadiusPx: Float,
    clearCenterRadiusPx: Float,
    centerZoneHasMaxBlur: Boolean,
    widthPx: Int,
    heightPx: Int,
    color: Color,
): RenderEffect? {
    if (widthPx <= 0 || heightPx <= 0) return null

    val w = max(1, widthPx).toFloat()
    val h = max(1, heightPx).toFloat()
    val minSide = min(w, h)
    val r = max(0f, maxRadiusPx)
    val clear = max(0f, clearCenterRadiusPx).coerceAtMost(minSide * 0.5f)
    val centerMode = if (centerZoneHasMaxBlur) 1f else 0f

    return remember(r, clear, centerMode, w, h, minSide, color) {
        val rtH = RuntimeShader(AlphaGaussianBlur61TapLocalRadialDynamicAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uWidth", w)
            setFloatUniform("uHeight", h)
            setFloatUniform("uMinSide", minSide)
            setFloatUniform("uClearCenterRadius", clear)
            setFloatUniform("uCenterMaxBlur", centerMode)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }

        val rtV = RuntimeShader(AlphaGaussianBlur61TapLocalRadialDynamicAglsl).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uWidth", w)
            setFloatUniform("uHeight", h)
            setFloatUniform("uMinSide", minSide)
            setFloatUniform("uClearCenterRadius", clear)
            setFloatUniform("uCenterMaxBlur", centerMode)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}
