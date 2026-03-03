package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agsladvanced

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Modifier.fireRectHaloShaderDraft(
    time: Float,
    bandWidth: Dp,
    cornerRadius: Dp,
    contourWidth: Dp,
    contourHeight: Dp,
    smokeScale: Float,
    intensity: Float = 1f,
    smokeOpacity: Float = 1f,
    coreScale: Float = 1f,
    smokeBlueTint: Float = 0f,
    thinMode: Float = 0f
): Modifier = composed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@composed this

    val density = LocalDensity.current
    val bandPx = with(density) { bandWidth.toPx() }
    val cornerPx = with(density) { cornerRadius.toPx() }
    val contourWidthPx = with(density) { contourWidth.toPx() }
    val contourHeightPx = with(density) { contourHeight.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

    val runtimeShader = remember { RuntimeShader(FireRectHaloAglsl) }
    val effect = remember(
        time,
        widthPx,
        heightPx,
        bandPx,
        cornerPx,
        contourWidthPx,
        contourHeightPx,
        smokeScale,
        intensity,
        smokeOpacity,
        coreScale,
        smokeBlueTint,
        thinMode
    ) {
        if (widthPx <= 0 || heightPx <= 0) return@remember null
        runtimeShader.setFloatUniform("uResolution", widthPx.toFloat(), heightPx.toFloat())
        runtimeShader.setFloatUniform("uTime", time)
        runtimeShader.setFloatUniform("uBandPx", bandPx)
        runtimeShader.setFloatUniform("uCornerPx", cornerPx)
        runtimeShader.setFloatUniform("uContourSize", contourWidthPx, contourHeightPx)
        runtimeShader.setFloatUniform("uSmokeScale", smokeScale)
        runtimeShader.setFloatUniform("uIntensity", intensity)
        runtimeShader.setFloatUniform("uSmokeOpacity", smokeOpacity)
        runtimeShader.setFloatUniform("uCoreScale", coreScale)
        runtimeShader.setFloatUniform("uSmokeBlueTint", smokeBlueTint)
        runtimeShader.setFloatUniform("uThinMode", thinMode)
        RenderEffect
            .createRuntimeShaderEffect(runtimeShader, "src")
            .asComposeRenderEffect()
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
                drawRect(color = Color.White.copy(alpha = 0.9f))
            }
        }
}
