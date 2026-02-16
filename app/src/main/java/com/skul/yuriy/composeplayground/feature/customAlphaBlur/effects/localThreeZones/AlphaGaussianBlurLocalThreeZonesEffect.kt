package com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.localThreeZones

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
import kotlin.math.max

fun Modifier.alphaGaussianBlurLocalThreeZone(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
    topOffset: Dp? = null,
    bottomOffset: Dp? = null,
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val radiusPx = with(density) { radius.toPx() }

    var heightPx by remember { mutableIntStateOf(0) }

    val computedTopPx = with(density) { topOffset?.toPx() } ?: (heightPx * 0.3333333f)
    val computedBottomPx = with(density) { bottomOffset?.toPx() } ?: (heightPx * 0.3333333f)
    val hasValidZones = heightPx > 0 && (computedTopPx + computedBottomPx) <= heightPx.toFloat()

    val effect = rememberAlphaGaussianBlurLocalThreeZoneEffect(
        radiusPx = radiusPx,
        heightPx = heightPx,
        topPx = computedTopPx,
        bottomPx = computedBottomPx,
        color = color,
        isValid = hasValidZones,
    )

    this
        .onSizeChanged { heightPx = it.height }
        .then(
            if (hasValidZones && effect != null) {
                Modifier.graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    renderEffect = effect.asComposeRenderEffect()
                }
            } else {
                Modifier
            }
        )
}


/**
 * This shader is reused for both passes.
 * The second pass is identified by the direction value (`uDir = 0,1`),
 * so it must always be invoked with that exact direction
 * to produce the tinted output correctly.
 */
private const val AlphaGaussianBlur17TapLocalThreeZoneAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uRadius;   // px
uniform float  uHeight;   // px
uniform float  uTopPx;    // px
uniform float  uBottomPx; // px
uniform float4 uColor;    // rgba

float isEdgeZone(float y) {
    float h = max(1.0, uHeight);
    float topEnd = clamp(uTopPx, 0.0, h);
    float bottomStart = clamp(h - uBottomPx, 0.0, h);

    if (y < topEnd) return 1.0;
    if (y >= bottomStart) return 1.0;
    return 0.0;
}

half4 main(float2 p) {
    float edge = isEdgeZone(p.y);
    float r = max(0.0, uRadius);

    if (edge < 0.5 || r < 0.5) {
        half a0 = src.eval(p).a;
        if (uDir.x > uDir.y) {
            return half4(0.0, 0.0, 0.0, a0);
        }
        return half4(uColor.rgb, a0 * half(uColor.a));
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
    // Assumption for this 2-pass setup:
    // pass #1 is horizontal (uDir = 1,0) and writes alpha mask,
    // pass #2 is vertical   (uDir = 0,1) and writes tinted output.
    if (uDir.x > uDir.y) {
        return half4(0.0, 0.0, 0.0, a);
    }
    return half4(uColor.rgb, a * half(uColor.a));
}
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaGaussianBlurLocalThreeZoneEffect(
    radiusPx: Float,
    heightPx: Int,
    topPx: Float,
    bottomPx: Float,
    color: Color,
    isValid: Boolean,
): RenderEffect? {
    if (!isValid || heightPx <= 0) return null

    val r = max(0f, radiusPx)
    val h = max(1, heightPx).toFloat()
    val tPx = max(0f, topPx)
    val bPx = max(0f, bottomPx)

    return remember(r, h, tPx, bPx, color, isValid) {
        val rtH = RuntimeShader(AlphaGaussianBlur17TapLocalThreeZoneAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uRadius", r)
            setFloatUniform("uHeight", h)
            setFloatUniform("uTopPx", tPx)
            setFloatUniform("uBottomPx", bPx)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }
        val rtV = RuntimeShader(AlphaGaussianBlur17TapLocalThreeZoneAglsl).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uRadius", r)
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
