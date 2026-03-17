package com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.dynamic

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

fun Modifier.alphaGaussianBlurByWidth(
    leftRadius: Dp,
    rightRadius: Dp,
    leftEdgeWidth: Dp,
    rightEdgeWidth: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val leftRadiusPx = with(density) { leftRadius.toPx() }
    val rightRadiusPx = with(density) { rightRadius.toPx() }
    val leftEdgeWidthPx = with(density) { leftEdgeWidth.toPx() }
    val rightEdgeWidthPx = with(density) { rightEdgeWidth.toPx() }
    val thresholdPxFor61Tap = with(density) { 16.dp.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }

    val effect = rememberAlphaGaussianBlurByWidthEffect(
        leftRadiusPx = leftRadiusPx,
        rightRadiusPx = rightRadiusPx,
        widthPx = widthPx,
        leftEdgeWidthPx = leftEdgeWidthPx,
        rightEdgeWidthPx = rightEdgeWidthPx,
        color = color,
        thresholdPxFor61Tap = thresholdPxFor61Tap,
    )

    this
        .onSizeChanged { widthPx = it.width }
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
            effect?.let { renderEffect = it.asComposeRenderEffect() }
        }
}

private const val AlphaGaussianBlur17TapByWidthAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uLeftRadius;   // px
uniform float  uRightRadius;  // px
uniform float  uWidth;        // px
uniform float  uLeftWidth;    // px
uniform float  uRightWidth;   // px

float localRadius(float x) {
    float w = max(1.0, uWidth);
    float left = max(0.0, uLeftWidth);
    float right = max(0.0, uRightWidth);

    if (x < left) {
        float k = clamp(x / max(1.0, left), 0.0, 1.0);
        return uLeftRadius * (1.0 - k);
    }

    float rightStart = max(left, w - right);
    if (x > rightStart) {
        float k = clamp((x - rightStart) / max(1.0, right), 0.0, 1.0);
        return uRightRadius * k;
    }

    return 0.0;
}

half4 main(float2 p) {
    float r = localRadius(p.x);

    if (r < 0.5) {
        return src.eval(p);
    }

    float sigma = max(0.001, r * 0.57735);
    float stepPx = max(0.25, r / 8.0);

    float4 sum = float4(0.0);
    float sumW = 0.0;

    for (int i = -8; i <= 8; i++) {
        float x = float(i) * stepPx;
        float w = exp(-(x * x) / (2.0 * sigma * sigma));
        sum += float4(src.eval(p + uDir * x)) * w;
        sumW += w;
    }

    return (sumW > 0.0) ? half4(sum / sumW) : src.eval(p);
}
"""

private const val AlphaGaussianBlur61TapByWidthAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uLeftRadius;   // px
uniform float  uRightRadius;  // px
uniform float  uWidth;        // px
uniform float  uLeftWidth;    // px
uniform float  uRightWidth;   // px

float localRadius(float x) {
    float w = max(1.0, uWidth);
    float left = max(0.0, uLeftWidth);
    float right = max(0.0, uRightWidth);

    if (x < left) {
        float k = clamp(x / max(1.0, left), 0.0, 1.0);
        return uLeftRadius * (1.0 - k);
    }

    float rightStart = max(left, w - right);
    if (x > rightStart) {
        float k = clamp((x - rightStart) / max(1.0, right), 0.0, 1.0);
        return uRightRadius * k;
    }

    return 0.0;
}

half4 main(float2 p) {
    float r = localRadius(p.x);

    if (r < 0.5) {
        return src.eval(p);
    }

    float sigma = max(0.001, r * 0.57735);
    float stepPx = max(0.125, r / 30.0);

    float4 sum = float4(0.0);
    float sumW = 0.0;

    for (int i = -30; i <= 30; i++) {
        float x = float(i) * stepPx;
        float w = exp(-(x * x) / (2.0 * sigma * sigma));
        sum += float4(src.eval(p + uDir * x)) * w;
        sumW += w;
    }

    return (sumW > 0.0) ? half4(sum / sumW) : src.eval(p);
}
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaGaussianBlurByWidthEffect(
    leftRadiusPx: Float,
    rightRadiusPx: Float,
    widthPx: Int,
    leftEdgeWidthPx: Float,
    rightEdgeWidthPx: Float,
    color: Color,
    thresholdPxFor61Tap: Float,
): RenderEffect? {
    if (widthPx <= 0) return null

    val resolvedLeftRadiusPx = max(0f, leftRadiusPx)
    val resolvedRightRadiusPx = max(0f, rightRadiusPx)
    val contentWidthPx = max(1, widthPx).toFloat()
    val leftPx = max(0f, leftEdgeWidthPx)
    val rightPx = max(0f, rightEdgeWidthPx)
    val maxRadiusPx = max(resolvedLeftRadiusPx, resolvedRightRadiusPx)
    val use61TapKernel = maxRadiusPx > thresholdPxFor61Tap

    val shaderSource = if (use61TapKernel) {
        AlphaGaussianBlur61TapByWidthAglsl
    } else {
        AlphaGaussianBlur17TapByWidthAglsl
    }

    return remember(
        resolvedLeftRadiusPx,
        resolvedRightRadiusPx,
        contentWidthPx,
        leftPx,
        rightPx,
        color,
        use61TapKernel
    ) {
        val rtH = RuntimeShader(shaderSource).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uLeftRadius", resolvedLeftRadiusPx)
            setFloatUniform("uRightRadius", resolvedRightRadiusPx)
            setFloatUniform("uWidth", contentWidthPx)
            setFloatUniform("uLeftWidth", leftPx)
            setFloatUniform("uRightWidth", rightPx)
        }

        val rtV = RuntimeShader(shaderSource).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uLeftRadius", resolvedLeftRadiusPx)
            setFloatUniform("uRightRadius", resolvedRightRadiusPx)
            setFloatUniform("uWidth", contentWidthPx)
            setFloatUniform("uLeftWidth", leftPx)
            setFloatUniform("uRightWidth", rightPx)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}
