package com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.static

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.max

fun Modifier.alphaGaussianBlur(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val rPx = with(density) { radius.toPx() * 1f }

    val effect = rememberAlphaGaussianBlurEffect(rPx, color)

    graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
        renderEffect = effect.asComposeRenderEffect()
    }
}


private const val AlphaGaussianBlur17TapAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uRadius; // px

half4 main(float2 p) {
    // Android-ish mapping
    float sigma = max(0.001, uRadius * 0.57735);
    float stepPx = max(0.5, uRadius / 8.0);

    float sum = 0.0;
    float sumW = 0.0;

    // Unrolled 17 taps: [-8..8]
    float x; float w;

    x = -8.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -7.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -6.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -5.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -4.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -3.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -2.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -1.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;

    x =  0.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p).a * w;             sumW += w;

    x =  1.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  2.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  3.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  4.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  5.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  6.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  7.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  8.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;

    float a = (sumW > 0.0) ? (sum / sumW) : 0.0;
    a = clamp(a, 0.0, 1.0);

    // Keep RGB empty; only alpha mask is meaningful for the next pass
    return half4(0.0, 0.0, 0.0, half(a));
}
"""


private const val AlphaGaussianBlur17TapTintAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uRadius; // px
uniform float4 uColor;  // rgba

half4 main(float2 p) {
    float sigma = max(0.001, uRadius * 0.57735);
    float stepPx = max(0.5, uRadius / 8.0);

    float sum = 0.0;
    float sumW = 0.0;

    float x; float w;

    x = -8.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -7.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -6.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -5.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -4.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -3.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -2.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x = -1.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;

    x =  0.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p).a * w;             sumW += w;

    x =  1.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  2.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  3.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  4.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  5.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  6.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  7.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;
    x =  8.0 * stepPx; w = exp(-(x*x) / (2.0 * sigma * sigma)); sum += src.eval(p + uDir * x).a * w; sumW += w;

    float a = (sumW > 0.0) ? (sum / sumW) : 0.0;
    a = clamp(a, 0.0, 1.0);

    // Apply tint + alpha
    half outA = half(a) * half(uColor.a);
    return half4(uColor.rgb, outA);
}
"""


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaGaussianBlurEffect(radiusPx: Float, color: Color): RenderEffect {
    val r = max(0f, radiusPx)

    return remember(r, color) {
        val rtH = RuntimeShader(AlphaGaussianBlur17TapAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uRadius", r)
        }
        val rtV = RuntimeShader(AlphaGaussianBlur17TapTintAglsl).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uRadius", r)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}
