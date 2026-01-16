package com.skul.yuriy.composeplayground.feature.metaballTextEdge

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.S)
val textMetaballRenderEffect: androidx.compose.ui.graphics.RenderEffect =
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
        RenderEffect.createBlurEffect(32f, 32f, Shader.TileMode.CLAMP)
    ).asComposeRenderEffect()



@RequiresApi(Build.VERSION_CODES.S)
val singleEffect: androidx.compose.ui.graphics.RenderEffect =

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
        ).asComposeRenderEffect()



@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun rememberTextMetaballRenderEffect(blurDp: Dp = 8.dp): androidx.compose.ui.graphics.RenderEffect {
    val blurPx = with(LocalDensity.current) { blurDp.toPx() }
     Log.e("WTF","dp = "+blurDp +"   px = "+blurPx)
    return remember(blurPx) {
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
            RenderEffect.createBlurEffect(blurPx, blurPx, Shader.TileMode.CLAMP)
        ).asComposeRenderEffect()
    }
}
