package com.skul.yuriy.composeplayground.util

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


/**
 * Applies a fading effect to the top and bottom edges of a composable using percentages of the
 * total height. This ensures that the content fades in or out smoothly when it reaches the top or bottom.
 *
 * The fading effect transitions from transparent to black at the top and from black to transparent
 * at the bottom, based on the provided percentages.
 *
 * No explicit pixel heights are used; instead, the gradient is applied based on the percentage of the total height.
 *
 * @param topFadePercentage The percentage of the height to apply as the top fade. Default is 10%.
 *                          If set to 0, no top fade will be applied.
 * @param bottomFadePercentage The percentage of the height to apply as the bottom fade. Default is 10%.
 *                             If set to 0, no bottom fade will be applied.
 * @return A modified [Modifier] that applies the fading effect to the top and bottom edges of the composable.
 */
fun Modifier.fadingTopBottomEdgesSimplified(
    topFadePercentage: Float = 0.1f,    // Default 10% fade for the top
    bottomFadePercentage: Float = 0.1f  // Default 10% fade for the bottom
): Modifier {
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // Required for blend modes to work correctly
        .drawWithContent {
            // Draw the content first
            drawContent()

            // Apply top fade based on percentage (no size.height usage)
            if (topFadePercentage > 0) {
                val topBrush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    topFadePercentage to Color.Black
                )
                drawRect(
                    brush = topBrush,
                    blendMode = BlendMode.DstIn
                )
            }

            // Apply bottom fade based on percentage (no size.height usage)
            if (bottomFadePercentage > 0) {
                val bottomBrush = Brush.verticalGradient(
                    (1 - bottomFadePercentage) to Color.Black,
                    1f to Color.Transparent
                )
                drawRect(
                    brush = bottomBrush,
                    blendMode = BlendMode.DstIn
                )
            }
        }
}

/**
 * Applies fading effects to the top and bottom of a composable based on the specified percentages
 * of the height. The fading transition is applied using a vertical gradient where the top
 * fades from transparent to black and the bottom fades from black to transparent.
 *
 * If the top or bottom fade percentage is `null` or `0`, no fade will be applied for that edge.
 *
 * @param topFadePercentage The percentage of the composable's height to apply as the top fade.
 *                          Default is 10%. If `null` or 0, no top fade will be applied.
 * @param bottomFadePercentage The percentage of the composable's height to apply as the bottom fade.
 *                             Default is 10%. If `null` or 0, no bottom fade will be applied.
 * @return A modified [Modifier] that applies fading effects to the top and bottom edges of the composable.
 */
fun Modifier.fadingTopBottomEdgesPercent(
    topFadePercentage: Float? = 0.1f,
    bottomFadePercentage: Float? = 0.1f
): Modifier {
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // Required for blend modes to work correctly
        .drawWithContent {
            drawContent()

            if (topFadePercentage != null && topFadePercentage > 0) {
                val topFadeHeight = size.height * topFadePercentage  // Calculate top fade height in pixels
                val topBrush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color.Black,
                    startY = 0f,
                    endY = topFadeHeight
                )
                drawRect(
                    brush = topBrush,
                    blendMode = BlendMode.DstIn,
                    size = Size(size.width, topFadeHeight)
                )
            }

            if (bottomFadePercentage != null && bottomFadePercentage > 0) {
                val bottomFadeHeight = size.height * bottomFadePercentage  // Calculate bottom fade height in pixels
                val bottomBrush = Brush.verticalGradient(
                    0f to Color.Black,
                    1f to Color.Transparent,
                    startY = size.height - bottomFadeHeight,
                    endY = size.height
                )
                drawRect(
                    brush = bottomBrush,
                    blendMode = BlendMode.DstIn,
                    topLeft = Offset(0f, size.height - bottomFadeHeight),
                    size = Size(size.width, bottomFadeHeight)
                )
            }
        }
}



/**
 * Applies fading effects to the top and bottom edges of a composable based on the provided height
 * in Dp units. The fading transition is applied using a vertical gradient where the top fades from
 * transparent to black, and the bottom fades from black to transparent.
 *
 * This function converts the given Dp values to pixels and delegates the actual fading logic to the
 * `fadingTopBottomEdgesPx` function.
 *
 * @param topFadeHeight The height in Dp for the top fade. Default is 24.dp. If set to 0.dp, no top fade will be applied.
 * @param bottomFadeHeight The height in Dp for the bottom fade. Default is 24.dp. If set to 0.dp, no bottom fade will be applied.
 * @return A modified [Modifier] that applies fading effects to the top and bottom edges of the composable.
 */
@Composable
fun Modifier.fadingTopBottomEdgesDp(
    topFadeHeight: Dp = 24.dp,
    bottomFadeHeight: Dp = 24.dp
): Modifier {
    val density = LocalDensity.current
    val topFadePx = with(density) { topFadeHeight.toPx() }
    val bottomFadePx = with(density) { bottomFadeHeight.toPx() }

    return this.fadingTopBottomEdgesPx(
        topFadePx = topFadePx,
        bottomFadePx = bottomFadePx
    )
}

/**
 * Applies fading effects to the top and bottom edges of a composable based on the provided pixel values.
 * The fading transition is applied using a vertical gradient where the top fades from transparent to black,
 * and the bottom fades from black to transparent.
 *
 * If the pixel value for either edge is 0 or less, no fade will be applied for that edge.
 *
 * @param topFadePx The height in pixels for the top fade. If set to 0 or less, no top fade will be applied.
 * @param bottomFadePx The height in pixels for the bottom fade. If set to 0 or less, no bottom fade will be applied.
 * @return A modified [Modifier] that applies fading effects to the top and bottom edges of the composable.
 */
fun Modifier.fadingTopBottomEdgesPx(
    topFadePx: Float,
    bottomFadePx: Float
): Modifier {
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // Required for blend modes to work correctly
        .drawWithContent {
            drawContent()

            if (topFadePx > 0) {
                val topBrush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color.Black,
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
                    0f to Color.Black,
                    1f to Color.Transparent,
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
