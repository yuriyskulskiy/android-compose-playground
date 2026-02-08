package com.skul.yuriy.composeplayground.feature.customBlur.test

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

//todo move somewhere

//this is AGSL style simple alpha gradienting from 0 to 1 by vertical for whole viewport
private const val VerticalAlphaFadeAglsl = """
uniform shader src;
uniform float uHeight; // px

half4 main(float2 p) {
    // Normalize Y to [0..1] across the current viewport height
    float h = max(1.0, uHeight);
    float t = clamp(p.y / h, 0.0, 1.0);

    // Top: 1.0, Bottom: 0.0
    half maskA = half(1.0 - t);

    half4 c = src.eval(p);

    // Apply mask to premultiplied color correctly (keep ratios)
    return half4(c.rgb * maskA, c.a * maskA);
}
"""

fun Modifier.verticalAlphaFadeTop1Bottom0(): Modifier = composed {
    if (Build.VERSION.SDK_INT < 33) return@composed this

    var heightPx by remember { mutableIntStateOf(0) }

    val shader = remember { RuntimeShader(VerticalAlphaFadeAglsl) }

    // Recreate RenderEffect when height changes (also avoids uniform update caching issues)
    val effect = remember(heightPx) {
        if (heightPx <= 0) return@remember null
        shader.setFloatUniform("uHeight", heightPx.toFloat())
        RenderEffect.createRuntimeShaderEffect(shader, "src")
    }

    this
        .onSizeChanged { heightPx = it.height }
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
            effect?.let { renderEffect = it.asComposeRenderEffect() }
        }
}
