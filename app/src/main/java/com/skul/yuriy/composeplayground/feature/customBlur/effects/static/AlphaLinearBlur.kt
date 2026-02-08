package com.skul.yuriy.composeplayground.feature.customBlur.effects.static

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

fun Modifier.alphaLinearBlur(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val rPx = with(density) { radius.toPx() * 0.8f }

    val effect = rememberAlphaLinearBlur17TapEffect(rPx, color)

    graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
        renderEffect = effect.asComposeRenderEffect()
    }
}

private const val AlphaLinearBlur17TapAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uRadius; // px

half4 main(float2 p) {
    float stepPx = max(0.25, uRadius / 8.0);

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
    return half4(a, a, a, a);
}
"""

private const val AlphaLinearBlur17TapTintAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uRadius; // px
uniform float4 uColor;  // rgba

half4 main(float2 p) {
    float stepPx = max(0.25, uRadius / 8.0);

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
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaLinearBlur17TapEffect(radiusPx: Float, color: Color): RenderEffect {
    val r = max(0f, radiusPx)

    return remember(r, color) {
        val rtH = RuntimeShader(AlphaLinearBlur17TapAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uRadius", r)
        }
        val rtV = RuntimeShader(AlphaLinearBlur17TapTintAglsl).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uRadius", r)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}


