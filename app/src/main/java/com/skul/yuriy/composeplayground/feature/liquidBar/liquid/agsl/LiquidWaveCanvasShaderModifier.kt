package com.skul.yuriy.composeplayground.feature.liquidBar.liquid.agsl

import android.graphics.BitmapShader
import android.graphics.Paint
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.InteractiveContentPosition
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.Wave1D

private const val LiquidWaveCanvasAglsl = """
uniform shader waveProfile; // R+G store normalized wave Y with 16-bit precision
uniform float uContainerWidthPx;
uniform float uProfileWidthPx;
uniform float uContainerHeightPx;
uniform float uBandPx;
uniform float uIsTop;
uniform float4 uWaveColor;

float decodeWaveYNormAt(float sampleIndex) {
    // Sample texel center to avoid accidental filtering of packed RG16 payload.
    half4 waveData = waveProfile.eval(float2(sampleIndex + 0.5, 0.5));
    float hi = floor(float(waveData.r) * 255.0 + 0.5);
    float lo = floor(float(waveData.g) * 255.0 + 0.5);
    return (hi * 256.0 + lo) / 65535.0;
}

half4 main(float2 p) {
    float profileX = (p.x / max(1.0, uContainerWidthPx)) * max(0.0, uProfileWidthPx - 1.0);
    float i0 = floor(profileX);
    float i1 = min(i0 + 1.0, max(0.0, uProfileWidthPx - 1.0));
    float t = profileX - i0;
    float y0 = decodeWaveYNormAt(i0);
    float y1 = decodeWaveYNormAt(i1);
    float yWaveNorm = mix(y0, y1, t);

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
    return half4(uWaveColor.rgb * mask, uWaveColor.a * mask);
}
"""

@Composable
internal fun Modifier.liquidWaveCanvasShader(
    frameTick: Int,
    containerSize: IntSize,
    interactiveContentPosition: InteractiveContentPosition,
    waveColor: Color,
    plotWidth: Float,
    scale: Float,
    yGain: Float,
    sim: Wave1D,
    bg: Color,
): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return this

    val width = containerSize.width
    val height = containerSize.height
    if (width <= 0 || height <= 0) return this
    val profileWidth = sim.n

    val waveProfileArgb = rememberWaveProfileArgb16(
        profileWidth = profileWidth,
        height = height,
        scale = scale,
        yGain = yGain,
        sim = sim
    )

    val safeScale = if (scale == 0f) 1f else scale
    val bandPx = ((plotWidth / safeScale) * height.toFloat()).coerceAtLeast(0f)

    val profileBitmap = remember(profileWidth) { createBitmap(profileWidth, 1) }
    val runtimeShader = remember { RuntimeShader(LiquidWaveCanvasAglsl) }
    val shaderPaint = remember { Paint() }

    profileBitmap.setPixels(waveProfileArgb, 0, profileWidth, 0, 0, profileWidth, 1)
    val profileShader = remember(profileBitmap) {
        BitmapShader(profileBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    runtimeShader.setInputShader("waveProfile", profileShader)
    runtimeShader.setFloatUniform("uContainerWidthPx", width.toFloat())
    runtimeShader.setFloatUniform("uProfileWidthPx", profileWidth.toFloat())
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

    shaderPaint.shader = runtimeShader

    val drawModifier = remember(
        runtimeShader,
        shaderPaint,
        frameTick,
        width,
        height,
        profileWidth,
        bandPx,
        waveColor,
        bg
    ) {
        Modifier.drawBehind {
            if (frameTick < 0) return@drawBehind
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRect(0f, 0f, size.width, size.height, shaderPaint)
            }
        }
    }
    return this.then(drawModifier)
}
