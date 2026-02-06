package com.skul.yuriy.composeplayground.feature.customBlur.test

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import kotlin.math.max

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

// 2%
@RequiresApi(Build.VERSION_CODES.S)
val alphaThreshold2PercentEffect =
    RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    // R
                    1f, 0f, 0f, 0f, 0f,
                    // G
                    0f, 1f, 0f, 0f, 0f,
                    // B
                    0f, 0f, 1f, 0f, 0f,
                    // A' = 160 * A - 816  → threshold ≈ 2%
                    0f, 0f, 0f, 160f, -816f
                )
            )
        )
    ).asComposeRenderEffect()


@RequiresApi(Build.VERSION_CODES.S)
val alphaThreshold5PercentEffect: androidx.compose.ui.graphics.RenderEffect =
    RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    // R
                    1f, 0f, 0f, 0f, 0f,
                    // G
                    0f, 1f, 0f, 0f, 0f,
                    // B
                    0f, 0f, 1f, 0f, 0f,
                    // A' = 160 * A - 2080  → threshold ≈ 5%
                    0f, 0f, 0f, 160f, -2080f
                )
            )
        )
    ).asComposeRenderEffect()

@RequiresApi(Build.VERSION_CODES.S)
val alphaThreshold20PercentEffect =
    RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    // R
                    1f, 0f, 0f, 0f, 0f,
                    // G
                    0f, 1f, 0f, 0f, 0f,
                    // B
                    0f, 0f, 1f, 0f, 0f,
                    // A' = 160 * A - 8160  → threshold ≈ 20%
                    0f, 0f, 0f, 160f, -8160f
                )
            )
        )
    ).asComposeRenderEffect()

//30%
@RequiresApi(Build.VERSION_CODES.S)
val alphaThreshold30PercentEffect: androidx.compose.ui.graphics.RenderEffect =
    RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    // R
                    1f, 0f, 0f, 0f, 0f,
                    // G
                    0f, 1f, 0f, 0f, 0f,
                    // B
                    0f, 0f, 1f, 0f, 0f,
                    // A' = 160 * A - 12320  → threshold ≈ 30%
                    0f, 0f, 0f, 160f, -12320f
                )
            )
        )
    ).asComposeRenderEffect()


// 50%
@RequiresApi(Build.VERSION_CODES.S)
val alphaThreshold50PercentEffect =
    RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    // R
                    1f, 0f, 0f, 0f, 0f,
                    // G
                    0f, 1f, 0f, 0f, 0f,
                    // B
                    0f, 0f, 1f, 0f, 0f,
                    // A' = 160 * A - 20400  → threshold ≈ 50%
                    0f, 0f, 0f, 160f, -20400f
                )
            )
        )
    ).asComposeRenderEffect()


// 70%
@RequiresApi(Build.VERSION_CODES.S)
val alphaThreshold70PercentEffect =
    RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    // R
                    1f, 0f, 0f, 0f, 0f,
                    // G
                    0f, 1f, 0f, 0f, 0f,
                    // B
                    0f, 0f, 1f, 0f, 0f,
                    // A' = 160 * A - 28560  → threshold ≈ 70%
                    0f, 0f, 0f, 160f, -28560f
                )
            )
        )
    ).asComposeRenderEffect()