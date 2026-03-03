package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import kotlin.math.min

// TODO draft
fun Modifier.drawOutlineRoundedRectShadowGradientDraft(
    color: Color,
    haloBorderWidth: Dp,
    cornerRadius: Dp
): Modifier = composed {
    val transparentColor = remember(color) { color.copy(alpha = 0f) }
    val linearStops = remember(color) { listOf(transparentColor, color) }
    val cornerStops = remember {
        arrayOf(
            0f to Color.Transparent,
            0f to Color.Transparent,
            0f to Color.Transparent,
            1f to Color.Transparent
        )
    }

    this.drawWithCache {
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

        val stripWidth = (w - 2f * r).coerceAtLeast(0f)
        val stripHeight = (h - 2f * r).coerceAtLeast(0f)
        val outerRadius = r + haloPx
        val edgeRatio = if (outerRadius > 0f) (r / outerRadius).coerceIn(0f, 1f) else 0f

        val sideBrush = Brush.verticalGradient(
            colors = linearStops,
            startY = -haloPx,
            endY = 0f
        )

        // Update reusable corner stops.
        cornerStops[0] = 0f to transparentColor
        cornerStops[1] = edgeRatio to transparentColor
        cornerStops[2] = edgeRatio to color
        cornerStops[3] = 1f to transparentColor

        val topLeftBrush = Brush.radialGradient(
            colorStops = cornerStops,
            center = Offset(r, r),
            radius = outerRadius
        )
        onDrawBehind {

            if (stripWidth > 0f) {
                withTransform({
                    translate(left = r, top = 0f)
                }) {
                    drawRect(
                        brush = sideBrush,
                        topLeft = Offset(0f, -haloPx),
                        size = Size(stripWidth, haloPx)
                    )
                }
                withTransform({
                    translate(left = r, top = h)
                    scale(scaleX = 1f, scaleY = -1f, pivot = Offset.Zero)
                }) {
                    drawRect(
                        brush = sideBrush,
                        topLeft = Offset(0f, -haloPx),
                        size = Size(stripWidth, haloPx)
                    )
                }
            }

            if (stripHeight > 0f) {
                withTransform({
                    translate(left = 0f, top = r + stripHeight)
                    rotate(degrees = -90f, pivot = Offset.Zero)
                }) {
                    drawRect(
                        brush = sideBrush,
                        topLeft = Offset(0f, -haloPx),
                        size = Size(stripHeight, haloPx)
                    )
                }
                withTransform({
                    translate(left = w, top = r)
                    rotate(degrees = 90f, pivot = Offset.Zero)
                }) {
                    drawRect(
                        brush = sideBrush,
                        topLeft = Offset(0f, -haloPx),
                        size = Size(stripHeight, haloPx)
                    )
                }
            }

            if (outerRadius > 0f) {
                fun drawTopLeftCorner() {
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
                }

                // Top-left (base corner).
                drawTopLeftCorner()

                // Top-right from base corner: translate + 90deg rotation.
                withTransform({
                    translate(left = w - 2f * r, top = 0f)
                    rotate(degrees = 90f, pivot = Offset(r, r))
                }) {
                    drawTopLeftCorner()
                }

                // Bottom-right from base corner: translate + 180deg rotation.
                withTransform({
                    translate(left = w - 2f * r, top = h - 2f * r)
                    rotate(degrees = 180f, pivot = Offset(r, r))
                }) {
                    drawTopLeftCorner()
                }

                // Bottom-left from base corner: translate + 270deg rotation.
                withTransform({
                    translate(left = 0f, top = h - 2f * r)
                    rotate(degrees = 270f, pivot = Offset(r, r))
                }) {
                    drawTopLeftCorner()
                }
            }
        }
    }
}
