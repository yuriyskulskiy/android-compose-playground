package com.skul.yuriy.composeplayground.feature.customBlur.util

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asComposeRenderEffect

/**
 * RenderEffects that hard-threshold alpha at different cutoffs (2%..70%).
 * Use for quick alpha masking/cleanup after blur.
 */
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
val alphaThreshold5PercentEffect =
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

@RequiresApi(Build.VERSION_CODES.S)
val alphaThreshold30PercentEffect =
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
