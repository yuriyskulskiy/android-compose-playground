package com.skul.yuriy.composeplayground.feature.scrollEdge.fadingEdge

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.fadingTopBottomEdges(
    topBrush: Brush? = Brush.verticalGradient(
        0f to Color.Transparent,
        0.17f to Color.Black
    ),
    bottomBrush: Brush? = Brush.verticalGradient(
        0.83f to Color.Black,
        1f to Color.Transparent
    )
): Modifier {
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // Required for blend modes to work correctly
        .drawWithContent {
            drawContent() // Draw the content first
            topBrush?.let { drawRect(it, blendMode = BlendMode.DstIn) } // Apply top fade if topBrush is not null
            bottomBrush?.let { drawRect(it, blendMode = BlendMode.DstIn) } // Apply bottom fade if bottomBrush is not null
        }
}