package com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.dynamic

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

fun Modifier.alphaLinearBlurByHeight(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.99f),
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val maxRadiusPx = with(density) { radius.toPx() }

    var heightPx by remember { mutableIntStateOf(0) }

    val effect = rememberAlphaLinearBlurByHeightEffect(
        maxRadiusPx = maxRadiusPx,
        heightPx = heightPx,
        color = color
    )

    this
        .onSizeChanged { heightPx = it.height }
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
            renderEffect = effect.asComposeRenderEffect()
        }
}

private const val AlphaLinearBlur17TapByHeightAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px

half4 main(float2 p) {
    float h = max(1.0, uHeight);
    float t = clamp(p.y / h, 0.0, 1.0);
    float r = uMaxRadius * t;

    // No blur at the top (or very small radius)
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

private const val AlphaLinearBlur17TapByHeightTintAglsl = """
uniform shader src;
uniform float2 uDir;
uniform float  uMaxRadius; // px
uniform float  uHeight;    // px
uniform float4 uColor;     // rgba

half4 main(float2 p) {
    float h = max(1.0, uHeight);
    float t = clamp(p.y / h, 0.0, 1.0);
    float r = uMaxRadius * t;

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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberAlphaLinearBlurByHeightEffect(
    maxRadiusPx: Float,
    heightPx: Int,
    color: Color
): RenderEffect {
    val r = max(0f, maxRadiusPx)
    val h = max(1, heightPx).toFloat()

    return remember(r, h, color) {
        val rtH = RuntimeShader(AlphaLinearBlur17TapByHeightAglsl).apply {
            setFloatUniform("uDir", 1f, 0f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
        }

        val rtV = RuntimeShader(AlphaLinearBlur17TapByHeightTintAglsl).apply {
            setFloatUniform("uDir", 0f, 1f)
            setFloatUniform("uMaxRadius", r)
            setFloatUniform("uHeight", h)
            setFloatUniform("uColor", color.red, color.green, color.blue, color.alpha)
        }

        val eH = RenderEffect.createRuntimeShaderEffect(rtH, "src")
        val eV = RenderEffect.createRuntimeShaderEffect(rtV, "src")
        RenderEffect.createChainEffect(eV, eH)
    }
}


