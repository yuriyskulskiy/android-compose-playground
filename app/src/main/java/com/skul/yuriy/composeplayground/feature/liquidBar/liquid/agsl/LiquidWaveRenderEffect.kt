package com.skul.yuriy.composeplayground.feature.liquidBar.liquid.agsl

import android.graphics.BitmapShader
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect as ComposeRenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.InteractiveContentPosition
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.Wave_1D

private const val LiquidWaveRenderAglsl = """
uniform shader src;
uniform shader waveProfile; // R+G store normalized wave Y with 16-bit precision
uniform float uContainerHeightPx;
uniform float uBandPx;
uniform float uIsTop;
uniform float4 uWaveColor;

half4 main(float2 p) {
    half4 c = src.eval(p);
    half4 waveData = waveProfile.eval(float2(p.x, 0.5));
    float hi = floor(float(waveData.r) * 255.0 + 0.5);
    float lo = floor(float(waveData.g) * 255.0 + 0.5);
    float yWaveNorm = (hi * 256.0 + lo) / 65535.0;
    float yNorm = clamp(p.y / max(1.0, uContainerHeightPx), 0.0, 1.0);
    float bandNorm = clamp(uBandPx / max(1.0, uContainerHeightPx), 0.0, 1.0);

    float m;
    if (uIsTop > 0.5) {
        if (bandNorm < 0.0001) {
            m = 1.0 - step(yWaveNorm, yNorm);
        } else {
            m = 1.0 - smoothstep(yWaveNorm, yWaveNorm + bandNorm, yNorm);
        }
    } else {
        if (bandNorm < 0.0001) {
            m = step(yWaveNorm, yNorm);
        } else {
            m = smoothstep(yWaveNorm - bandNorm, yWaveNorm, yNorm);
        }
    }
    m = clamp(m, 0.0, 1.0);

    half mask = half(m);
    half4 wave = half4(uWaveColor.rgb * mask, uWaveColor.a * mask);
    half4 srcMasked = half4(c.rgb * mask, c.a * mask);

    // Premultiplied source-over: masked src over liquid.
    return srcMasked + wave * (half(1.0) - srcMasked.a);
}
"""

@Composable
internal fun rememberLiquidWaveRenderEffectOrNull(
    frameTick: Int,
    containerSize: IntSize,
    interactiveContentPosition: InteractiveContentPosition,
    waveColor: Color,
    plotWidth: Float,
    scale: Float,
    yGain: Float,
    sim: Wave_1D,
): ComposeRenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null

    val width = containerSize.width
    val height = containerSize.height
    if (width <= 0 || height <= 0) return null

    val waveProfileArgb = rememberWaveProfileArgb16(
        width = width,
        height = height,
        scale = scale,
        yGain = yGain,
        sim = sim
    )

    val safeScale = if (scale == 0f) 1f else scale
    val bandPx = ((plotWidth / safeScale) * height.toFloat()).coerceAtLeast(0f)

    val profileBitmap = remember(width) { createBitmap(width, 1) }
    val runtimeShader = remember { RuntimeShader(LiquidWaveRenderAglsl) }
    profileBitmap.setPixels(waveProfileArgb, 0, width, 0, 0, width, 1)
    val profileShader = remember(profileBitmap) {
        BitmapShader(profileBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    runtimeShader.setInputShader("waveProfile", profileShader)
    runtimeShader.setFloatUniform("uContainerHeightPx", height.toFloat())
    runtimeShader.setFloatUniform("uBandPx", bandPx)
    runtimeShader.setFloatUniform(
        "uIsTop",
        if (interactiveContentPosition == InteractiveContentPosition.Top) 1f else 0f
    )
    runtimeShader.setFloatUniform(
        "uWaveColor",
        waveColor.red,
        waveColor.green,
        waveColor.blue,
        waveColor.alpha
    )

    return remember(runtimeShader, frameTick, width, height, bandPx, waveColor) {
        RenderEffect
            .createRuntimeShaderEffect(runtimeShader, "src")
            .asComposeRenderEffect()
    }
}
