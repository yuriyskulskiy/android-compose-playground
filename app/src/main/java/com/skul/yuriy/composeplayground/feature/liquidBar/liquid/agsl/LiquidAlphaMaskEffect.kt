package com.skul.yuriy.composeplayground.feature.liquidBar.liquid.agsl

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.RenderEffect as ComposeRenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.core.graphics.createBitmap

private const val LiquidAlphaMaskAglsl = """
uniform shader src;
uniform shader waveProfile; // alpha stores wave Y in root-normalized [0..1]
uniform float uContentHeightPx;
uniform float uZoneTopNorm;
uniform float uZoneHeightNorm;

half4 main(float2 p) {
    half4 c = src.eval(p);
    float yWaveNorm = waveProfile.eval(float2(p.x, 0.5)).a;
    float yLocalNorm = clamp(p.y / max(1.0, uContentHeightPx), 0.0, 1.0);
    float yRootNorm = uZoneTopNorm + yLocalNorm * uZoneHeightNorm;
    half m = half(step(yWaveNorm, yRootNorm));
    return half4(c.rgb * m, c.a * m);
}
"""

/**
 * Builds a per-frame AGSL mask effect for liquid navigation content.
 *
 * Wave profile is encoded into a 1-row bitmap (alpha = wave Y normalized),
 * then AGSL compares each content pixel Y with sampled wave Y at the same X.
 *
 * `waveProfileArgb` should contain exactly `containerWidthPx` pixels for a 1-row mask.
 * `frameTick` is used as a recomposition key to refresh effect per frame.
 */
@Composable
internal fun rememberLiquidAlphaMaskEffect(
    frameTick: Int,
    containerWidthPx: Int,
    containerHeightPx: Int,
    hitZoneHeightPx: Int,
    hitZoneTopPx: Int,
    waveProfileArgb: IntArray,
): ComposeRenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null
    if (containerWidthPx <= 0 || containerHeightPx <= 0 || hitZoneHeightPx <= 0) return null

    val width = containerWidthPx
    val contentHeight = hitZoneHeightPx
    val zoneTopPx = hitZoneTopPx.coerceIn(0, (containerHeightPx - contentHeight).coerceAtLeast(0))
    if (waveProfileArgb.size < width) return null

    val profileBitmap = remember(width) {
        createBitmap(width, 1)
    }
    val runtimeShader = remember { RuntimeShader(LiquidAlphaMaskAglsl) }
    profileBitmap.setPixels(waveProfileArgb, 0, width, 0, 0, width, 1)
    val profileShader = remember(profileBitmap) {
        BitmapShader(profileBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }
    runtimeShader.setInputShader("waveProfile", profileShader)
    runtimeShader.setFloatUniform("uContentHeightPx", contentHeight.toFloat())
    runtimeShader.setFloatUniform(
        "uZoneTopNorm",
        zoneTopPx.toFloat() / containerHeightPx.toFloat()
    )
    runtimeShader.setFloatUniform(
        "uZoneHeightNorm",
        contentHeight.toFloat() / containerHeightPx.toFloat()
    )

    return remember(runtimeShader, frameTick, width, contentHeight) {
        RenderEffect
            .createRuntimeShaderEffect(runtimeShader, "src")
            .asComposeRenderEffect()
    }
}
