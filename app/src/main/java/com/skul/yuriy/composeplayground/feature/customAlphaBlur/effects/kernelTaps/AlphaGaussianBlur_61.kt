package com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.kernelTaps

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

fun Modifier.alphaGaussianBlurTest61(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val rPx = with(density) { radius.toPx() * 1f }

    val effect = rememberAlphaGaussianBlurEffect61(radiusPx = rPx, color = color)

    graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
        renderEffect = effect.asComposeRenderEffect()
    }
}

private const val AlphaGaussianBlur61TapAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uRadius; // px

half4 main(float2 p) {
    float sigma = max(0.001, uRadius * 0.57735);
    float stepPx = max(0.125, uRadius / 30.0);

    float sum = 0.0;
    float sumW = 0.0;

    for (int i = -30; i <= 30; i++) {
        float x = float(i) * stepPx;
        float w = exp(-(x * x) / (2.0 * sigma * sigma));
        sum += src.eval(p + uDir * x).a * w;
        sumW += w;
    }

    float a = (sumW > 0.0) ? (sum / sumW) : 0.0;
    a = clamp(a, 0.0, 1.0);
    return half4(0.0, 0.0, 0.0, half(a));
}
"""

private const val AlphaGaussianBlur61TapTintAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uRadius; // px
uniform float4 uColor;  // rgba

half4 main(float2 p) {
    float sigma = max(0.001, uRadius * 0.57735);
    float stepPx = max(0.125, uRadius / 30.0);

    float sum = 0.0;
    float sumW = 0.0;

    for (int i = -30; i <= 30; i++) {
        float x = float(i) * stepPx;
        float w = exp(-(x * x) / (2.0 * sigma * sigma));
        sum += src.eval(p + uDir * x).a * w;
        sumW += w;
    }

    float a = (sumW > 0.0) ? (sum / sumW) : 0.0;
    a = clamp(a, 0.0, 1.0);

    half outA = half(a) * half(uColor.a);
    return half4(uColor.rgb, outA);
}
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaGaussianBlurEffect61(
    radiusPx: Float,
    color: Color,
): RenderEffect {
    val r = max(0f, radiusPx)

    return remember(r, color) {
        val rtH = RuntimeShader(AlphaGaussianBlur61TapAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uRadius", r)
        }
        val rtV = RuntimeShader(AlphaGaussianBlur61TapTintAglsl).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uRadius", r)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}
