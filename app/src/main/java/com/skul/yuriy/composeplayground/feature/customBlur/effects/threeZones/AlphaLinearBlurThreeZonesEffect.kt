package com.skul.yuriy.composeplayground.feature.customBlur.effects.threeZones
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

// ----------------------------- Public Modifier -----------------------------

/**
 * @param radius Max blur radius (at the very top and the very bottom edges).
 * @param color Tint color applied in the second pass (alpha mask * tint alpha).
 * @param topOffset Optional fixed top zone height. If null, defaults to 33% of the viewport height.
 * @param bottomOffset Optional fixed bottom zone height. If null, defaults to 33% of the viewport height.
 */
fun Modifier.alphaLinearBlurThreeZone(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
    topOffset: Dp? = null,
    bottomOffset: Dp? = null,
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val maxRadiusPx = with(density) { radius.toPx() }

    var heightPx by remember { mutableIntStateOf(0) }

    // Defaults: 33% / (middle) / 33%
    val computedTopPx = with(density) { topOffset?.toPx() } ?: (heightPx * 0.3333333f)
    val computedBottomPx = with(density) { bottomOffset?.toPx() } ?: (heightPx * 0.3333333f)

    val effect = rememberAlphaLinearBlurThreeZoneEffect(
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
 * Linear (uniform weights) alpha blur with 3 vertical zones:
 * - Top zone: blur radius decreases from maxRadius -> 0
 * - Middle zone: no blur
 * - Bottom zone: blur radius increases from 0 -> maxRadius
 *
 * Zones can be defined either:
 * - By percentages (default): top=33% height, middle=34% height, bottom=33% height
 * - By absolute offsets (topOffset/bottomOffset in dp): top zone = topOffset, bottom zone = bottomOffset,
 *   middle zone = remaining height.
 */

// ----------------------------- AGSL (Pass 1: alpha blur) -----------------------------

private const val AlphaLinearBlur17TapThreeZoneAglsl = """
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

    // Clamp zones so they never overlap
    float midStart = top;
    float midEnd = max(midStart, h - bottom);

    if (y < midStart) {
        // Top zone: maxR -> 0
        float k = clamp(y / max(1.0, top), 0.0, 1.0);
        return uMaxRadius * (1.0 - k);
    }

    if (y <= midEnd) {
        // Middle zone: no blur
        return 0.0;
    }

    // Bottom zone: 0 -> maxR
    float k = clamp((y - midEnd) / max(1.0, bottom), 0.0, 1.0);
    return uMaxRadius * k;
}

half4 main(float2 p) {
    float r = localRadius(p.y);

    // No blur: return original alpha mask
    if (r < 0.5) {
        half a0 = src.eval(p).a;
        return half4(0.0, 0.0, 0.0, a0);
    }

    float stepPx = max(0.25, r / 8.0);

    half s = 0.0;
    s += src.eval(p + uDir * (-8.0 * stepPx)).a;
    s += src.eval(p + uDir * (-7.0 * stepPx)).a;
    s += src.eval(p + uDir * (-6.0 * stepPx)).a;
    s += src.eval(p + uDir * (-5.0 * stepPx)).a;
    s += src.eval(p + uDir * (-4.0 * stepPx)).a;
    s += src.eval(p + uDir * (-3.0 * stepPx)).a;
    s += src.eval(p + uDir * (-2.0 * stepPx)).a;
    s += src.eval(p + uDir * (-1.0 * stepPx)).a;

    s += src.eval(p).a;

    s += src.eval(p + uDir * ( 1.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 2.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 3.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 4.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 5.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 6.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 7.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 8.0 * stepPx)).a;

    half a = s / half(17.0);
    return half4(0.0, 0.0, 0.0, a);
}
"""

// ----------------------------- AGSL (Pass 2: alpha blur + tint) -----------------------------

private const val AlphaLinearBlur17TapThreeZoneTintAglsl = """
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

    float stepPx = max(0.25, r / 8.0);

    half s = 0.0;
    s += src.eval(p + uDir * (-8.0 * stepPx)).a;
    s += src.eval(p + uDir * (-7.0 * stepPx)).a;
    s += src.eval(p + uDir * (-6.0 * stepPx)).a;
    s += src.eval(p + uDir * (-5.0 * stepPx)).a;
    s += src.eval(p + uDir * (-4.0 * stepPx)).a;
    s += src.eval(p + uDir * (-3.0 * stepPx)).a;
    s += src.eval(p + uDir * (-2.0 * stepPx)).a;
    s += src.eval(p + uDir * (-1.0 * stepPx)).a;

    s += src.eval(p).a;

    s += src.eval(p + uDir * ( 1.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 2.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 3.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 4.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 5.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 6.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 7.0 * stepPx)).a;
    s += src.eval(p + uDir * ( 8.0 * stepPx)).a;

    half a = (s / half(17.0)) * half(uColor.a);
    return half4(uColor.rgb, a);
}
"""

// ----------------------------- RenderEffect builder -----------------------------

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaLinearBlurThreeZoneEffect(
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
        val rtH = RuntimeShader(AlphaLinearBlur17TapThreeZoneAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
            setFloatUniform("uTopPx", tPx)
            setFloatUniform("uBottomPx", bPx)
        }

        val rtV = RuntimeShader(AlphaLinearBlur17TapThreeZoneTintAglsl).apply {
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


