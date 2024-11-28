package com.skul.yuriy.composeplayground.feature.metaballBlur

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asComposeRenderEffect
import com.skul.yuriy.composeplayground.feature.metaballBlur.model.RenderEffectEntity

@RequiresApi(Build.VERSION_CODES.S)
val metaBallRenderEffect: androidx.compose.ui.graphics.RenderEffect =
    RenderEffect.createChainEffect(
        RenderEffect.createColorFilterEffect(
            ColorMatrixColorFilter(
                ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f, 0f,
                        0f, 0f, 1f, 0f, 0f,
                        0f, 0f, 0f, 160f, -10000f
                    )
                )
            )
        ),
        RenderEffect.createBlurEffect(100f, 100f, Shader.TileMode.MIRROR)
    ).asComposeRenderEffect()

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val metaBallOutlineAGSLRenderEffect = RenderEffect.createChainEffect(
    RenderEffect.createRuntimeShaderEffect(
        RuntimeShader(AgslOutlineBorderShaderSource),
        "composable"    // Specify the input name
    ),
    RenderEffect.createBlurEffect(100f, 100f, Shader.TileMode.MIRROR), // Add blur effect,

).asComposeRenderEffect()

// Provides a map of render effects
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun generateRenderEffectMap(): Map<Int, RenderEffectEntity> {
    return listOf(
        RenderEffectEntity(
            id = 1,
            renderEffect = metaBallOutlineAGSLRenderEffect,
            displayName = "Blur+AGSL Filter"
        ),
        RenderEffectEntity(
            id = 2,
            renderEffect = metaBallRenderEffect,
            displayName = "Blur+Color Filter"
        )
    ).associateBy { it.id }
}