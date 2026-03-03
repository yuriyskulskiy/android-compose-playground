package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agslsimple

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

fun Modifier.drawOutlineSimpleAgslShadow(
    color: Color,
    cornerRadius: Dp,
    maxHaloBorderWidth: Dp,
    intensity: Float,
    press: Float,
    renderMode: SimpleAgslRenderMode
): Modifier = composed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@composed this

    val density = LocalDensity.current
    val cornerPx = with(density) { cornerRadius.toPx() }
    val strokeWidthPx = with(density) { 2.dp.toPx() }
    val idleFadeEndPx = with(density) { 8.dp.toPx() }
    val maxHaloBorderWidthPx = with(density) { maxHaloBorderWidth.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

    fun applyUniforms(shader: RuntimeShader) {
        shader.setFloatUniform("uResolution", widthPx.toFloat(), heightPx.toFloat())
        shader.setFloatUniform("uCornerPx", cornerPx)
        shader.setFloatUniform("uStrokeWidthPx", strokeWidthPx.coerceAtLeast(0.001f))
        shader.setFloatUniform("uIdleFadeEndPx", idleFadeEndPx.coerceAtLeast(0.001f))
        shader.setFloatUniform(
            "uMaxHaloBorderWidthPx",
            maxHaloBorderWidthPx.coerceAtLeast(0.001f)
        )
        shader.setFloatUniform("uPress", press)
        shader.setFloatUniform("uIntensity", intensity)
        shader.setFloatUniform(
            "uColor",
            color.red,
            color.green,
            color.blue,
            color.alpha
        )
    }

    when (renderMode) {
        SimpleAgslRenderMode.RenderEffect -> {
            val runtimeShaderForEffect = remember { RuntimeShader(SimpleRectHaloAgsl) }
            val effect = if (widthPx > 0 && heightPx > 0) {
                applyUniforms(runtimeShaderForEffect)
                RenderEffect
                    .createRuntimeShaderEffect(runtimeShaderForEffect, "src")
                    .asComposeRenderEffect()
            } else {
                null
            }

            this
                .onSizeChanged {
                    widthPx = it.width
                    heightPx = it.height
                }
                .then(
                    if (widthPx > 0 && heightPx > 0) {
                        Modifier.graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                            effect?.let { renderEffect = it }
                        }
                    } else {
                        Modifier
                    }
                )
                .drawBehind {
                    if (widthPx > 0 && heightPx > 0 && effect != null) {
                        // Source input for RuntimeShader effect
                        drawRect(color = Color.White.copy(alpha = 1f))
                    }
                }
        }
        SimpleAgslRenderMode.CanvasPaint -> {
            val runtimeShaderForCanvas = remember { RuntimeShader(SimpleRectHaloAgsl) }
            val nativePaint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG) }
            this
                .onSizeChanged {
                    widthPx = it.width
                    heightPx = it.height
                }
                .drawWithContent {
                    if (widthPx > 0 && heightPx > 0) {
                        applyUniforms(runtimeShaderForCanvas)
                        nativePaint.shader = runtimeShaderForCanvas
                        val overflow = maxHaloBorderWidthPx
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawRect(
                                -overflow,
                                -overflow,
                                size.width + overflow,
                                size.height + overflow,
                                nativePaint
                            )
                        }
                    }
                    drawContent()
                }
        }
    }
}

private const val SimpleRectHaloAgsl = """
uniform shader src;
uniform float2 uResolution;
uniform float uCornerPx;
uniform float uStrokeWidthPx;
uniform float uIdleFadeEndPx;
uniform float uMaxHaloBorderWidthPx;
uniform float uPress;
uniform float uIntensity;
uniform float4 uColor;

float sdRoundBox(float2 p, float2 b, float r) {
    float2 q = abs(p) - b + float2(r);
    return length(max(q, float2(0.0))) + min(max(q.x, q.y), 0.0) - r;
}

half4 main(float2 fragCoord) {
//    half4 base = src.eval(fragCoord);

    float2 center = uResolution * 0.5;
    float2 p = fragCoord - center;

    // Full-rect shape in pixel space
    float2 halfSize = (uResolution * 0.5) - float2(1.0);
    float radius = clamp(uCornerPx, 0.0, min(halfSize.x, halfSize.y) - 1.0);

    float signedD = sdRoundBox(p, halfSize, radius);

    // Outside-only distance
    float dOut = max(signedD, 0.0);
    float outsideMask = step(0.0, signedD);

    float d = max(dOut, 0.0);
    float coreW = max(uStrokeWidthPx, 0.001);
    float idleFadeEnd = max(uIdleFadeEndPx, coreW + 0.001);
    float pressSolidEnd = coreW;
    float pressDropEnd = coreW * 3.0;
    float pressTailStart = pressDropEnd;
    float pressTailEnd = max(uMaxHaloBorderWidthPx, pressTailStart + 0.001);

    float idleBand = 0.0;
    if (d <= coreW) {
        idleBand = 0.5;
    } else if (d <= idleFadeEnd) {
        float t = (d - coreW) / max(idleFadeEnd - coreW, 0.001);
        idleBand = mix(0.5, 0.0, t);
    } else {
        idleBand = 0.0;
    }

    float pressBand = 0.0;
    if (d <= pressSolidEnd) {
        pressBand = 1.0;
    } else if (d <= pressDropEnd) {
        float t = (d - pressSolidEnd) / max(pressDropEnd - pressSolidEnd, 0.001);
        pressBand = mix(1.0, 0.5, t);
    } else {
        float t = clamp((d - pressTailStart) / max(pressTailEnd - pressTailStart, 0.001), 0.0, 1.0);
        pressBand = mix(0.5, 0.0, t);
    }

    float band = mix(idleBand, pressBand, clamp(uPress, 0.0, 1.0)) * outsideMask;
    float a = clamp(band, 0.0, 1.0);
    float3 rgb = uColor.rgb * (a * uIntensity);
    return half4(half3(rgb), half(a * uColor.a)) ;
}
"""
