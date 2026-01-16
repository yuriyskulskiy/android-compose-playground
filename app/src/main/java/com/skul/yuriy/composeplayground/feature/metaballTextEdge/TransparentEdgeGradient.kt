package com.skul.yuriy.composeplayground.feature.metaballTextEdge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.gradientTopButtonEdges(
    topFadeHeight: Dp = 24.dp,
    bottomFadeHeight: Dp = 24.dp
): Modifier {
    val density = LocalDensity.current
    val topFadePx = with(density) { topFadeHeight.toPx() }
    val bottomFadePx = with(density) { bottomFadeHeight.toPx() }

    return this.gradientTopButtonEdgesPx(
        topFadePx = topFadePx,
        bottomFadePx = bottomFadePx
    )
}

fun Modifier.gradientTopButtonEdgesPx(
    topFadePx: Float,
    bottomFadePx: Float
): Modifier {
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // Required for blend modes to work correctly
        .drawWithContent {
            drawContent()

            if (topFadePx > 0) {
                val topBrush = Brush.verticalGradient(
                    0f to Color.Black,
                    1f to Color.Transparent,
                    startY = 0f,
                    endY = topFadePx
                )
                drawRect(
                    brush = topBrush,
                    size = Size(size.width, topFadePx),
                    blendMode = BlendMode.DstIn
                )
            }

            if (bottomFadePx > 0) {
                val bottomBrush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color.Black,
                    startY = size.height - bottomFadePx,
                    endY = size.height
                )
                drawRect(
                    brush = bottomBrush,
                    topLeft = Offset(0f, size.height - bottomFadePx),
                    size = Size(size.width, bottomFadePx),
                    blendMode = BlendMode.DstIn
                )
            }
        }
}