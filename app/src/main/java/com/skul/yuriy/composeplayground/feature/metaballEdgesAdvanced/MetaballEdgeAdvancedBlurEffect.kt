package com.skul.yuriy.composeplayground.feature.metaballEdgesAdvanced

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

fun Modifier.metaballEdgeAdvancedGaussianBlur(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
    topOffset: Dp? = null,
    bottomOffset: Dp? = null,
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val maxRadiusPx = with(density) { radius.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

    val computedTopPx = with(density) { topOffset?.toPx() } ?: (heightPx * 0.3333333f)
    val computedBottomPx = with(density) { bottomOffset?.toPx() } ?: (heightPx * 0.3333333f)

    val effect = rememberMetaballEdgeAdvancedGaussianBlurEffect(
        maxRadiusPx = maxRadiusPx,
        widthPx = widthPx,
        heightPx = heightPx,
        topPx = computedTopPx,
        bottomPx = computedBottomPx,
        color = color,
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

private const val HorizontalMaskAglsl = """
uniform shader src;
uniform float  uMaxRadius;
uniform float  uWidth;
uniform float  uHeight;
uniform float  uTopPx;
uniform float  uBottomPx;

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

float2 clampToBounds(float2 point) {
    float maxX = max(0.0, uWidth - 1.0);
    float maxY = max(0.0, uHeight - 1.0);
    return float2(
        clamp(point.x, 0.0, maxX),
        clamp(point.y, 0.0, maxY)
    );
}

half4 main(float2 p) {
    float r = localRadius(p.y);
    float2 basePos = clampToBounds(p);

    if (r < 0.5) {
        half a0 = src.eval(basePos).a;
        return half4(0.0, 0.0, 0.0, a0);
    }

    float sigma = max(0.001, r * 0.57735);
    float stepPx = max(0.125, r / 30.0);

    float sum = 0.0;
    float sumW = 0.0;

    for (int i = -30; i <= 30; i++) {
        float dx = float(i) * stepPx;
        float w = exp(-(dx * dx) / (2.0 * sigma * sigma));
        float2 samplePos = clampToBounds(p + float2(dx, 0.0));
        sum += src.eval(samplePos).a * w;
        sumW += w;
    }

    half a = (sumW > 0.0) ? half(sum / sumW) : half(0.0);
    return half4(0.0, 0.0, 0.0, a);
}
"""

private const val VerticalTintRenormalizedAglsl = """
uniform shader src;
uniform float  uMaxRadius;
uniform float  uWidth;
uniform float  uHeight;
uniform float  uTopPx;
uniform float  uBottomPx;
uniform float4 uColor;

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

float clampX(float value) {
    float maxX = max(0.0, uWidth - 1.0);
    return clamp(value, 0.0, maxX);
}

half4 main(float2 p) {
    float r = localRadius(p.y);
    float px = clampX(p.x);
    float clampedY = clamp(p.y, 0.0, max(0.0, uHeight - 1.0));

    if (r < 0.5) {
        half a0 = src.eval(float2(px, clampedY)).a * half(uColor.a);
        return half4(uColor.rgb, a0);
    }

    float sigma = max(0.001, r * 0.57735);
    float stepPx = max(0.125, r / 30.0);

    float sum = 0.0;
    float validW = 0.0;

    for (int i = -30; i <= 30; i++) {
        float dy = float(i) * stepPx;
        float sampleY = p.y + dy;

        if (sampleY < 0.0 || sampleY > (uHeight - 1.0)) {
            continue;
        }

        float w = exp(-(dy * dy) / (2.0 * sigma * sigma));
        sum += src.eval(float2(px, sampleY)).a * w;
        validW += w;
    }

    half a = (validW > 0.0) ? half(sum / validW) * half(uColor.a) : half(0.0);
    return half4(uColor.rgb, a);
}
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberMetaballEdgeAdvancedGaussianBlurEffect(
    maxRadiusPx: Float,
    widthPx: Int,
    heightPx: Int,
    topPx: Float,
    bottomPx: Float,
    color: Color,
): RenderEffect? {
    if (widthPx <= 0 || heightPx <= 0) return null

    val r = max(0f, maxRadiusPx)
    val w = max(1, widthPx).toFloat()
    val h = max(1, heightPx).toFloat()
    val tPx = max(0f, topPx)
    val bPx = max(0f, bottomPx)

    return remember(r, w, h, tPx, bPx, color) {
        val rtH = RuntimeShader(HorizontalMaskAglsl).apply {
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uWidth", w)
            setFloatUniform("uHeight", h)
            setFloatUniform("uTopPx", tPx)
            setFloatUniform("uBottomPx", bPx)
        }

        val rtV = RuntimeShader(VerticalTintRenormalizedAglsl).apply {
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uWidth", w)
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
