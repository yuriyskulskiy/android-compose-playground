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
 * Adds fading edges to the top and bottom of a composable using percentages of the height to determine the fade size.
 * If the percentage for either edge is null or 0, the corresponding edge fade will not be drawn.
 *
 * The fading effect transitions from transparent to black for the top fade and from black to transparent for the bottom fade.
 *
 * @param topFadePercentage The percentage of the composable's height to apply as the top fade. Default is 20%.
 *                          If null or 0, no top fade will be applied.
 * @param bottomFadePercentage The percentage of the composable's height to apply as the bottom fade. Default is 20%.
 *                             If null or 0, no bottom fade will be applied.
 * @return A modified [Modifier] that applies fading effects to the top and bottom edges of the composable.
 */
fun Modifier.fadingTopBottomEdgesSimplified(
    topFadePercentage: Float? = 0.20f,
    bottomFadePercentage: Float? = 0.20f
): Modifier {
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // Required for blend modes to work correctly
        .drawWithContent {
            drawContent()

            val heightPx = size.height

            if (topFadePercentage != null && topFadePercentage > 0) {
                val topFadeHeight = heightPx * topFadePercentage

                val topBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black
                    ),
                    startY = 0f,
                    endY = topFadeHeight
                )

                drawRect(
                    brush = topBrush,
                    size = Size(size.width, topFadeHeight),
                    blendMode = BlendMode.DstIn
                )
            }

            if (bottomFadePercentage != null && bottomFadePercentage > 0) {
                val bottomFadeHeight = heightPx * bottomFadePercentage

                val bottomBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color.Transparent
                    ),
                    startY = heightPx - bottomFadeHeight,
                    endY = heightPx
                )

                drawRect(
                    brush = bottomBrush,
                    topLeft = Offset(0f, heightPx - bottomFadeHeight),
                    size = Size(size.width, bottomFadeHeight),
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
 *                          Default is 27%. If `null` or 0, no top fade will be applied.
 * @param bottomFadePercentage The percentage of the composable's height to apply as the bottom fade.
 *                             Default is 27%. If `null` or 0, no bottom fade will be applied.
 * @return A modified [Modifier] that applies fading effects to the top and bottom edges of the composable.
 */
fun Modifier.fadingTopBottomEdgesPercent(
    topFadePercentage: Float? = 0.27f,
    bottomFadePercentage: Float? = 0.27f
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
