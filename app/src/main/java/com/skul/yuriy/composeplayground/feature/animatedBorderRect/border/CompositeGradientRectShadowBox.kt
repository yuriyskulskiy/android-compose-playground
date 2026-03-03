package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import kotlin.math.min

// TODO draft
fun Modifier.drawOutlineRoundedRectShadowGradientDraft(
    color: Color,
    haloBorderWidth: Dp,
    cornerRadius: Dp
): Modifier = this.drawWithCache {
        val haloPx = haloBorderWidth.toPx()
        if (haloPx < 1f) {
            return@drawWithCache onDrawBehind {}
        }

        val radiusPx = cornerRadius.toPx().coerceAtLeast(0f)
        val maxCorner = min(size.width, size.height) / 2f
        val r = min(radiusPx, maxCorner)
        val w = size.width
        val h = size.height

        if (w <= 0f || h <= 0f) {
            return@drawWithCache onDrawBehind {}
        }

        val transparent = color.copy(alpha = 0f)
        val stripWidth = (w - 2f * r).coerceAtLeast(0f)
        val stripHeight = (h - 2f * r).coerceAtLeast(0f)
        val outerRadius = r + haloPx
        val edgeRatio = if (outerRadius > 0f) (r / outerRadius).coerceIn(0f, 1f) else 0f

        val topBrush = Brush.verticalGradient(
            colors = listOf(transparent, color),
            startY = -haloPx,
            endY = 0f
        )
        val bottomBrush = Brush.verticalGradient(
            colors = listOf(color, transparent),
            startY = h,
            endY = h + haloPx
        )
        val leftBrush = Brush.horizontalGradient(
            colors = listOf(transparent, color),
            startX = -haloPx,
            endX = 0f
        )
        val rightBrush = Brush.horizontalGradient(
            colors = listOf(color, transparent),
            startX = w,
            endX = w + haloPx
        )
        val topLeftBrush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to transparent,
                edgeRatio to transparent,
                edgeRatio to color,
                1f to transparent
            ),
            center = Offset(r, r),
            radius = outerRadius
        )
        val topRightBrush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to transparent,
                edgeRatio to transparent,
                edgeRatio to color,
                1f to transparent
            ),
            center = Offset(w - r, r),
            radius = outerRadius
        )
        val bottomRightBrush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to transparent,
                edgeRatio to transparent,
                edgeRatio to color,
                1f to transparent
            ),
            center = Offset(w - r, h - r),
            radius = outerRadius
        )
        val bottomLeftBrush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to transparent,
                edgeRatio to transparent,
                edgeRatio to color,
                1f to transparent
            ),
            center = Offset(r, h - r),
            radius = outerRadius
        )


        onDrawBehind {

            if (stripWidth > 0f) {
                drawRect(
                    brush = topBrush,
                    topLeft = Offset(r, -haloPx),
                    size = Size(stripWidth, haloPx)
                )
                drawRect(
                    brush = bottomBrush,
                    topLeft = Offset(r, h),
                    size = Size(stripWidth, haloPx)
                )
            }

            if (stripHeight > 0f) {
                drawRect(
                    brush = leftBrush,
                    topLeft = Offset(-haloPx, r),
                    size = Size(haloPx, stripHeight)
                )
                drawRect(
                    brush = rightBrush,
                    topLeft = Offset(w, r),
                    size = Size(haloPx, stripHeight)
                )
            }

            if (outerRadius > 0f) {
                clipRect(
                    left = -haloPx,
                    top = -haloPx,
                    right = r,
                    bottom = r
                ) {
                    drawCircle(
                        brush = topLeftBrush,
                        radius = outerRadius,
                        center = Offset(r, r)
                    )
                }

                clipRect(
                    left = w - r,
                    top = -haloPx,
                    right = w + haloPx,
                    bottom = r
                ) {
                    drawCircle(
                        brush = topRightBrush,
                        radius = outerRadius,
                        center = Offset(w - r, r)
                    )
                }

                clipRect(
                    left = w - r,
                    top = h - r,
                    right = w + haloPx,
                    bottom = h + haloPx
                ) {
                    drawCircle(
                        brush = bottomRightBrush,
                        radius = outerRadius,
                        center = Offset(w - r, h - r)
                    )
                }

                clipRect(
                    left = -haloPx,
                    top = h - r,
                    right = r,
                    bottom = h + haloPx
                ) {
                    drawCircle(
                        brush = bottomLeftBrush,
                        radius = outerRadius,
                        center = Offset(r, h - r)
                    )
                }
            }
        }
    }
