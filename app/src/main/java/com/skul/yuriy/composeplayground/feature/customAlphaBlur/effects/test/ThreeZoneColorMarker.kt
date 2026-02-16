package com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.test

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val ThreeZoneColorMarkerAglsl = """
uniform shader src;
uniform float uHeight; // px
uniform float uTopPx; // px
uniform float uBottomPx; // px

half4 main(float2 p) {
    float h = max(1.0, uHeight);
    float topEnd = clamp(uTopPx, 0.0, h);
    float bottomStart = clamp(h - uBottomPx, 0.0, h);

    if (p.y < topEnd) {
        // Top: yellow
        return half4(1.0, 1.0, 0.0, 1.0);
    } else if (p.y >= bottomStart) {
        // Bottom: violet
        return half4(0.7, 0.0, 1.0, 1.0);
    } else {
        // Middle: green
        return half4(0.0, 1.0, 0.0, 1.0);
    }
}
"""

fun Modifier.threeZoneColorMarkerTopMiddleBottom(
    topOffset: Dp = 150.dp,
    bottomOffset: Dp = 150.dp,
): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    val density = LocalDensity.current
    val topPx = with(density) { topOffset.toPx() }
    val bottomPx = with(density) { bottomOffset.toPx() }

    var heightPx by remember { mutableIntStateOf(0) }
    val shader = remember { RuntimeShader(ThreeZoneColorMarkerAglsl) }
    val hasValidZones = heightPx > 0 && (topPx + bottomPx) <= heightPx.toFloat()

    val effect = remember(heightPx, topPx, bottomPx, hasValidZones) {
        if (!hasValidZones) return@remember null
        shader.setFloatUniform("uHeight", heightPx.toFloat())
        shader.setFloatUniform("uTopPx", topPx)
        shader.setFloatUniform("uBottomPx", bottomPx)
        RenderEffect.createRuntimeShaderEffect(shader, "src")
    }

    this
        .onSizeChanged { heightPx = it.height }
        .then(
            if (hasValidZones && effect != null) {
                Modifier.graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    renderEffect = effect.asComposeRenderEffect()
                }
            } else {
                Modifier
            }
        )
}
