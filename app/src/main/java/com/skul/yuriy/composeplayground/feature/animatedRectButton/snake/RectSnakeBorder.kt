package com.skul.yuriy.composeplayground.feature.animatedRectButton.snake

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

enum class RectSnakeTrackPlacement {
    INSIDE,
    CENTER_ON_EDGE,
    OUTSIDE
}

private enum class RectSnakeSegment {
    TopEdge,
    TopRightArc,
    RightEdge,
    BottomRightArc,
    BottomEdge,
    BottomLeftArc,
    LeftEdge,
    TopLeftArc,
}

fun Modifier.rectSnakeBorder(
    bodyColorFrom: Color = Color.Green.copy(alpha = 0f),
    bodyColorTo: Color = Color.Green,
    glowColorFrom: Color = Color.Green.copy(alpha = 0f),
    glowColorTo: Color = Color.Green.copy(alpha = 0.8f),
    progress: Float = 0f,
    cornerRadius: Dp = 28.dp,
    snakeLengthFraction: Float = 0.28f,
    bodyStrokeWidth: Dp = 2.dp,
    glowingShadowWidth: Dp = 12.dp,
    glowingBlurRadius: Dp = glowingShadowWidth / 2,
    trackPlacement: RectSnakeTrackPlacement = RectSnakeTrackPlacement.CENTER_ON_EDGE
): Modifier = this.drawWithCache {
    val geometry = buildRectSnakeTrackGeometry(
        size = size,
        bodyStrokeWidthPx = bodyStrokeWidth.toPx(),
        glowingStrokeWidthPx = glowingShadowWidth.toPx(),
        glowingBlurRadiusPx = glowingBlurRadius.toPx(),
        cornerRadiusPx = cornerRadius.toPx(),
        trackPlacement = trackPlacement
    )
    if (geometry == null) {
        return@drawWithCache onDrawBehind {}
    }
    val bodyStrokeWidthPx = geometry.bodyStrokeWidthPx
    val glowingStrokeWidthPx = geometry.glowingStrokeWidthPx
    val glowingBlurRadiusPx = geometry.glowingBlurRadiusPx
    val segmentLengths = geometry.segmentLengths
    val segmentStarts = geometry.segmentStarts
    val totalLen = geometry.totalLen
    val snakeState = buildRectSnakeProgressState(
        progress = progress,
        snakeLengthFraction = snakeLengthFraction,
        totalLen = totalLen
    )
    val bodyStrokePaint = Paint().apply {
        isAntiAlias = true
        color = bodyColorTo.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = bodyStrokeWidthPx
        strokeCap = Paint.Cap.BUTT
        strokeJoin = Paint.Join.ROUND
    }
    val glowStrokePaint = Paint().apply {
        isAntiAlias = true
        color = glowColorTo.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = glowingStrokeWidthPx
        strokeCap = Paint.Cap.BUTT
        strokeJoin = Paint.Join.ROUND
        maskFilter = BlurMaskFilter(glowingBlurRadiusPx, BlurMaskFilter.Blur.NORMAL)
    }
    val glowHeadPaint = Paint().apply {
        isAntiAlias = true
        color = glowColorTo.toArgb()
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(glowingBlurRadiusPx, BlurMaskFilter.Blur.NORMAL)
    }

    val head = pointAtDistance(geometry, snakeState.headDistance)
    onDrawBehind {
        if (snakeState.snakeLength > 0f) {
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                // Render the wider glow layer first, then draw the thin body snake on top of it.
                drawSnakeLayerNative(
                    canvas = nativeCanvas,
                    geometry = geometry,
                    snakeState = snakeState,
                    colorFrom = glowColorFrom,
                    colorTo = glowColorTo,
                    paint = glowStrokePaint
                )
                nativeCanvas.drawCircle(
                    head.x,
                    head.y,
                    glowingStrokeWidthPx / 2f,
                    glowHeadPaint
                )
                drawSnakeLayerNative(
                    canvas = nativeCanvas,
                    geometry = geometry,
                    snakeState = snakeState,
                    colorFrom = bodyColorFrom,
                    colorTo = bodyColorTo,
                    paint = bodyStrokePaint
                )
            }

            drawCircle(
                color = bodyColorTo,
                radius = bodyStrokeWidthPx / 2f,
                center = head
            )
        }
    }
}

/**
 * Internal geometry snapshot for [rectSnakeBorder].
 *
 * This is a cache-scoped helper container used only to group precomputed track values for the
 * current draw pass. It is intentionally a regular class, not a data class:
 * the instance is not used for value-based comparisons, and it stores [FloatArray] fields whose
 * default data-class equality/hashCode semantics would be misleading here.
 */
private class RectSnakeTrackGeometry(
    val bodyStrokeWidthPx: Float,
    val glowingStrokeWidthPx: Float,
    val glowingBlurRadiusPx: Float,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val radius: Float,
    val topLen: Float,
    val rightLen: Float,
    val arcQuarterLen: Float,
    val segmentLengths: FloatArray,
    val segmentStarts: FloatArray,
    val totalLen: Float,
    val topRightCenter: Offset,
    val bottomRightCenter: Offset,
    val bottomLeftCenter: Offset,
    val topLeftCenter: Offset
)

private class RectSnakeProgressState(
    val headDistance: Float,
    val snakeLength: Float,
    val tailDistance: Float,
    val alphaAtDistance: (Float) -> Float
)

private fun buildRectSnakeTrackGeometry(
    size: androidx.compose.ui.geometry.Size,
    bodyStrokeWidthPx: Float,
    glowingStrokeWidthPx: Float,
    glowingBlurRadiusPx: Float,
    cornerRadiusPx: Float,
    trackPlacement: RectSnakeTrackPlacement
): RectSnakeTrackGeometry? {
    val maxHalfStroke = max(bodyStrokeWidthPx, glowingStrokeWidthPx) / 2f
    val borderInset = when (trackPlacement) {
        RectSnakeTrackPlacement.INSIDE -> maxHalfStroke
        RectSnakeTrackPlacement.CENTER_ON_EDGE -> 0f
        RectSnakeTrackPlacement.OUTSIDE -> -maxHalfStroke
    }
    val left = borderInset
    val top = borderInset
    val right = size.width - borderInset
    val bottom = size.height - borderInset

    val trackWidth = right - left
    val trackHeight = bottom - top
    if (trackWidth <= 0f || trackHeight <= 0f) return null

    val rawTrackRadius = (cornerRadiusPx - borderInset).coerceAtLeast(0f)
    val radius = min(rawTrackRadius, min(trackWidth, trackHeight) / 2f)
    val topLen = (trackWidth - 2f * radius).coerceAtLeast(0f)
    val rightLen = (trackHeight - 2f * radius).coerceAtLeast(0f)
    val arcQuarterLen = (PI.toFloat() * radius / 2f).coerceAtLeast(0f)
    val segmentLengths = floatArrayOf(
        topLen,
        arcQuarterLen,
        rightLen,
        arcQuarterLen,
        topLen,
        arcQuarterLen,
        rightLen,
        arcQuarterLen
    )
    val segmentStarts = FloatArray(segmentLengths.size)
    var totalLen = 0f
    for (index in segmentLengths.indices) {
        segmentStarts[index] = totalLen
        totalLen += segmentLengths[index]
    }
    if (totalLen <= 0f) return null

    return RectSnakeTrackGeometry(
        bodyStrokeWidthPx = bodyStrokeWidthPx,
        glowingStrokeWidthPx = glowingStrokeWidthPx,
        glowingBlurRadiusPx = glowingBlurRadiusPx,
        left = left,
        top = top,
        right = right,
        bottom = bottom,
        radius = radius,
        topLen = topLen,
        rightLen = rightLen,
        arcQuarterLen = arcQuarterLen,
        segmentLengths = segmentLengths,
        segmentStarts = segmentStarts,
        totalLen = totalLen,
        topRightCenter = Offset(right - radius, top + radius),
        bottomRightCenter = Offset(right - radius, bottom - radius),
        bottomLeftCenter = Offset(left + radius, bottom - radius),
        topLeftCenter = Offset(left + radius, top + radius)
    )
}

private fun pointAtDistance(
    geometry: RectSnakeTrackGeometry,
    rawDistance: Float
): Offset {
    val segmentStarts = geometry.segmentStarts
    val segmentLengths = geometry.segmentLengths
    val totalLen = geometry.totalLen
    val distance = ((rawDistance % totalLen) + totalLen) % totalLen
    var segmentIndex = 0
    for (index in segmentStarts.indices) {
        val segmentEnd = segmentStarts[index] + segmentLengths[index]
        if (distance <= segmentEnd || index == segmentStarts.lastIndex) {
            segmentIndex = index
            break
        }
    }
    val local = (distance - segmentStarts[segmentIndex]).coerceAtLeast(0f)
    val radius = geometry.radius
    return when (segmentIndex) {
        0 -> Offset(geometry.left + radius + local, geometry.top)
        1 -> {
            if (radius <= 0f) {
                Offset(geometry.right, geometry.top)
            } else {
                val angle = (-PI / 2f + local / radius).toFloat()
                pointOnCircle(geometry.topRightCenter, radius, angle)
            }
        }

        2 -> Offset(geometry.right, geometry.top + radius + local)
        3 -> {
            if (radius <= 0f) {
                Offset(geometry.right, geometry.bottom)
            } else {
                val angle = local / radius
                pointOnCircle(geometry.bottomRightCenter, radius, angle)
            }
        }

        4 -> Offset(geometry.right - radius - local, geometry.bottom)
        5 -> {
            if (radius <= 0f) {
                Offset(geometry.left, geometry.bottom)
            } else {
                val angle = ((PI / 2f) + local / radius).toFloat()
                pointOnCircle(geometry.bottomLeftCenter, radius, angle)
            }
        }

        6 -> Offset(geometry.left, geometry.bottom - radius - local)
        else -> {
            if (radius <= 0f) {
                Offset(geometry.left, geometry.top)
            } else {
                val angle = (PI + local / radius).toFloat()
                pointOnCircle(geometry.topLeftCenter, radius, angle)
            }
        }
    }
}

private fun pointOnCircle(center: Offset, radius: Float, angleRadians: Float): Offset = Offset(
    x = center.x + radius * cos(angleRadians),
    y = center.y + radius * sin(angleRadians)
)

private fun segmentAt(index: Int): RectSnakeSegment = RectSnakeSegment.entries[index]

private fun colorAtDistance(
    distance: Float,
    from: Color,
    to: Color,
    snakeState: RectSnakeProgressState,
): Color = colorLerp(from, to, snakeState.alphaAtDistance(distance))

private fun buildNativeSweepShader(
    center: Offset,
    startAngleDegrees: Float,
    sweepDegrees: Float,
    startColor: Color,
    endColor: Color
): android.graphics.SweepGradient {
    val sweepStop = (sweepDegrees / 360f).coerceIn(0f, 1f)
    return android.graphics.SweepGradient(
        center.x,
        center.y,
        intArrayOf(
            startColor.toArgb(),
            endColor.toArgb(),
            endColor.toArgb()
        ),
        floatArrayOf(0f, sweepStop, 1f)
    ).also { shader ->
        val shaderMatrix = Matrix()
        shaderMatrix.setRotate(startAngleDegrees, center.x, center.y)
        shader.setLocalMatrix(shaderMatrix)
    }
}

private fun drawSnakeLayerNative(
    canvas: android.graphics.Canvas,
    geometry: RectSnakeTrackGeometry,
    snakeState: RectSnakeProgressState,
    colorFrom: Color,
    colorTo: Color,
    paint: Paint,
) {
    when {
        snakeState.snakeLength >= geometry.totalLen -> {
            drawDistanceIntervalNative(
                canvas = canvas,
                geometry = geometry,
                snakeState = snakeState,
                startDistance = 0f,
                endDistance = geometry.totalLen,
                colorFrom = colorFrom,
                colorTo = colorTo,
                paint = paint
            )
        }
        snakeState.tailDistance >= 0f -> {
            drawDistanceIntervalNative(
                canvas = canvas,
                geometry = geometry,
                snakeState = snakeState,
                startDistance = snakeState.tailDistance,
                endDistance = snakeState.headDistance,
                colorFrom = colorFrom,
                colorTo = colorTo,
                paint = paint
            )
        }
        else -> {
            drawDistanceIntervalNative(
                canvas = canvas,
                geometry = geometry,
                snakeState = snakeState,
                startDistance = snakeState.tailDistance + geometry.totalLen,
                endDistance = geometry.totalLen,
                colorFrom = colorFrom,
                colorTo = colorTo,
                paint = paint
            )
            drawDistanceIntervalNative(
                canvas = canvas,
                geometry = geometry,
                snakeState = snakeState,
                startDistance = 0f,
                endDistance = snakeState.headDistance,
                colorFrom = colorFrom,
                colorTo = colorTo,
                paint = paint
            )
        }
    }
}

private fun drawDistanceIntervalNative(
    canvas: android.graphics.Canvas,
    geometry: RectSnakeTrackGeometry,
    snakeState: RectSnakeProgressState,
    startDistance: Float,
    endDistance: Float,
    colorFrom: Color,
    colorTo: Color,
    paint: Paint,
) {
    if (endDistance <= startDistance) return
    for (segmentIndex in geometry.segmentStarts.indices) {
        val segmentStart = geometry.segmentStarts[segmentIndex]
        val segmentEnd = segmentStart + geometry.segmentLengths[segmentIndex]
        if (segmentEnd <= segmentStart) continue

        val overlapStart = max(startDistance, segmentStart)
        val overlapEnd = min(endDistance, segmentEnd)
        if (overlapEnd <= overlapStart) continue

        val localStart = overlapStart - segmentStart
        val localEnd = overlapEnd - segmentStart
        val startColor = colorAtDistance(overlapStart, colorFrom, colorTo, snakeState)
        val endColor = colorAtDistance(overlapEnd, colorFrom, colorTo, snakeState)
        drawSegmentPartNative(
            canvas = canvas,
            segment = segmentAt(segmentIndex),
            geometry = geometry,
            localStart = localStart,
            localEnd = localEnd,
            startColor = startColor,
            endColor = endColor,
            paint = paint
        )
    }
}

private fun drawSegmentPartNative(
    canvas: android.graphics.Canvas,
    segment: RectSnakeSegment,
    geometry: RectSnakeTrackGeometry,
    localStart: Float,
    localEnd: Float,
    startColor: Color,
    endColor: Color,
    paint: Paint
) {
    if (localEnd <= localStart || paint.strokeWidth <= 0f) return
    val left = geometry.left
    val top = geometry.top
    val right = geometry.right
    val bottom = geometry.bottom
    val radius = geometry.radius
    when (segment) {
        RectSnakeSegment.TopEdge -> {
            val x0 = left + radius + localStart
            val y0 = top
            val x1 = left + radius + localEnd
            val y1 = top
            paint.shader = LinearGradient(
                x0,
                y0,
                x1,
                y1,
                startColor.toArgb(),
                endColor.toArgb(),
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(x0, y0, x1, y1, paint)
        }
        RectSnakeSegment.TopRightArc -> if (radius > 0f) {
            val startAngle = -90f + (localStart / radius) * 180f / PI.toFloat()
            val sweep = ((localEnd - localStart) / radius) * 180f / PI.toFloat()
            drawArcSegmentNative(
                canvas = canvas,
                center = geometry.topRightCenter,
                radius = radius,
                startAngleDegrees = startAngle,
                sweepDegrees = sweep,
                startColor = startColor,
                endColor = endColor,
                paint = paint
            )
        }
        RectSnakeSegment.RightEdge -> {
            val x0 = right
            val y0 = top + radius + localStart
            val x1 = right
            val y1 = top + radius + localEnd
            paint.shader = LinearGradient(
                x0,
                y0,
                x1,
                y1,
                startColor.toArgb(),
                endColor.toArgb(),
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(x0, y0, x1, y1, paint)
        }
        RectSnakeSegment.BottomRightArc -> if (radius > 0f) {
            val startAngle = (localStart / radius) * 180f / PI.toFloat()
            val sweep = ((localEnd - localStart) / radius) * 180f / PI.toFloat()
            drawArcSegmentNative(
                canvas = canvas,
                center = geometry.bottomRightCenter,
                radius = radius,
                startAngleDegrees = startAngle,
                sweepDegrees = sweep,
                startColor = startColor,
                endColor = endColor,
                paint = paint
            )
        }
        RectSnakeSegment.BottomEdge -> {
            val x0 = right - radius - localStart
            val y0 = bottom
            val x1 = right - radius - localEnd
            val y1 = bottom
            paint.shader = LinearGradient(
                x0,
                y0,
                x1,
                y1,
                startColor.toArgb(),
                endColor.toArgb(),
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(x0, y0, x1, y1, paint)
        }
        RectSnakeSegment.BottomLeftArc -> if (radius > 0f) {
            val startAngle = 90f + (localStart / radius) * 180f / PI.toFloat()
            val sweep = ((localEnd - localStart) / radius) * 180f / PI.toFloat()
            drawArcSegmentNative(
                canvas = canvas,
                center = geometry.bottomLeftCenter,
                radius = radius,
                startAngleDegrees = startAngle,
                sweepDegrees = sweep,
                startColor = startColor,
                endColor = endColor,
                paint = paint
            )
        }
        RectSnakeSegment.LeftEdge -> {
            val x0 = left
            val y0 = bottom - radius - localStart
            val x1 = left
            val y1 = bottom - radius - localEnd
            paint.shader = LinearGradient(
                x0,
                y0,
                x1,
                y1,
                startColor.toArgb(),
                endColor.toArgb(),
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(x0, y0, x1, y1, paint)
        }
        RectSnakeSegment.TopLeftArc -> if (radius > 0f) {
            val startAngle = 180f + (localStart / radius) * 180f / PI.toFloat()
            val sweep = ((localEnd - localStart) / radius) * 180f / PI.toFloat()
            drawArcSegmentNative(
                canvas = canvas,
                center = geometry.topLeftCenter,
                radius = radius,
                startAngleDegrees = startAngle,
                sweepDegrees = sweep,
                startColor = startColor,
                endColor = endColor,
                paint = paint
            )
        }
    }
}

private fun drawArcSegmentNative(
    canvas: android.graphics.Canvas,
    center: Offset,
    radius: Float,
    startAngleDegrees: Float,
    sweepDegrees: Float,
    startColor: Color,
    endColor: Color,
    paint: Paint
) {
    if (radius <= 0f || sweepDegrees <= 0f || paint.strokeWidth <= 0f) return
    paint.shader = buildNativeSweepShader(
        center = center,
        startAngleDegrees = startAngleDegrees,
        sweepDegrees = sweepDegrees,
        startColor = startColor,
        endColor = endColor
    )
    canvas.drawArc(
        RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius),
        startAngleDegrees,
        sweepDegrees,
        false,
        paint
    )
    paint.shader = null
}

private fun colorLerp(start: Color, end: Color, fraction: Float): Color {
    val t = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * t,
        green = start.green + (end.green - start.green) * t,
        blue = start.blue + (end.blue - start.blue) * t,
        alpha = start.alpha + (end.alpha - start.alpha) * t
    )
}

private fun buildRectSnakeProgressState(
    progress: Float,
    snakeLengthFraction: Float,
    totalLen: Float
): RectSnakeProgressState {
    val normalizedProgress = ((progress % 1f) + 1f) % 1f
    val headDistance = normalizedProgress * totalLen
    val snakeLength = (snakeLengthFraction.coerceIn(0f, 1f) * totalLen).coerceAtLeast(0f)
    val tailDistance = if (snakeLength >= totalLen) 0f else headDistance - snakeLength
    val alphaAtDistance: (Float) -> Float = { distance ->
        if (snakeLength <= 0f) {
            0f
        } else if (tailDistance >= 0f) {
            ((distance - tailDistance) / snakeLength).coerceIn(0f, 1f)
        } else {
            val tailWrapped = tailDistance + totalLen
            val distanceFromTail = if (distance >= tailWrapped) {
                distance - tailWrapped
            } else {
                (totalLen - tailWrapped) + distance
            }
            (distanceFromTail / snakeLength).coerceIn(0f, 1f)
        }
    }
    return RectSnakeProgressState(
        headDistance = headDistance,
        snakeLength = snakeLength,
        tailDistance = tailDistance,
        alphaAtDistance = alphaAtDistance
    )
}
