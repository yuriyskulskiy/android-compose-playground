package com.skul.yuriy.composeplayground.util.shadowborder

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

fun Modifier.rectSnakeBorder(
    bodyColor: Color = Color.Green,
    glowShadowColor: Color = Color.Green.copy(alpha = 0.8f),
    progress: Float = 0f,
    cornerRadius: Dp = 28.dp,
    snakeLengthFraction: Float = 0.28f,
    bodyStrokeWidth: Dp = 2.dp,
    glowingShadowWidth: Dp = 12.dp,
    glowingBlurRadius: Dp = glowingShadowWidth / 2,
    trackPlacement: RectSnakeTrackPlacement = RectSnakeTrackPlacement.CENTER_ON_EDGE
): Modifier = this.drawWithCache {
    val bodyStrokeWidthPx = bodyStrokeWidth.toPx()
    val glowingStrokeWidthPx = glowingShadowWidth.toPx()
    val glowingBlurRadiusPx = glowingBlurRadius.toPx()
    val cornerRadiusPx = cornerRadius.toPx()

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
    if (trackWidth <= 0f || trackHeight <= 0f) {
        return@drawWithCache onDrawBehind {}
    }

    val rawTrackRadius = (cornerRadiusPx - borderInset).coerceAtLeast(0f)
    val r = min(rawTrackRadius, min(trackWidth, trackHeight) / 2f)
    val topLen = (trackWidth - 2f * r).coerceAtLeast(0f)
    val rightLen = (trackHeight - 2f * r).coerceAtLeast(0f)
    val arcQuarterLen = (PI.toFloat() * r / 2f).coerceAtLeast(0f)
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
    val segmentStarts = FloatArray(8)
    var totalLen = 0f
    for (index in segmentLengths.indices) {
        segmentStarts[index] = totalLen
        totalLen += segmentLengths[index]
    }
    if (totalLen <= 0f) {
        return@drawWithCache onDrawBehind {}
    }

    val trCenter = Offset(right - r, top + r)
    val brCenter = Offset(right - r, bottom - r)
    val blCenter = Offset(left + r, bottom - r)
    val tlCenter = Offset(left + r, top + r)

    fun anglePoint(center: Offset, angleRadians: Float): Offset = Offset(
            x = center.x + r * cos(angleRadians),
            y = center.y + r * sin(angleRadians)
        )

    fun pointAt(rawDistance: Float): Offset {
        var distance = ((rawDistance % totalLen) + totalLen) % totalLen
        var segmentIndex = 0
        for (index in segmentStarts.indices) {
            val segmentEnd = segmentStarts[index] + segmentLengths[index]
            if (distance <= segmentEnd || index == segmentStarts.lastIndex) {
                segmentIndex = index
                break
            }
        }
        val local = (distance - segmentStarts[segmentIndex]).coerceAtLeast(0f)
        return when (segmentIndex) {
            0 -> Offset(left + r + local, top)
            1 -> {
                if (r <= 0f) Offset(right, top) else {
                    val angle = (-PI / 2f + local / r).toFloat()
                    anglePoint(trCenter, angle)
                }
            }
            2 -> Offset(right, top + r + local)
            3 -> {
                if (r <= 0f) Offset(right, bottom) else {
                    val angle = local / r
                    anglePoint(brCenter, angle)
                }
            }
            4 -> Offset(right - r - local, bottom)
            5 -> {
                if (r <= 0f) Offset(left, bottom) else {
                    val angle = ((PI / 2f) + local / r).toFloat()
                    anglePoint(blCenter, angle)
                }
            }
            6 -> Offset(left, bottom - r - local)
            else -> {
                if (r <= 0f) Offset(left, top) else {
                    val angle = (PI + local / r).toFloat()
                    anglePoint(tlCenter, angle)
                }
            }
        }
    }

    fun DrawScope.drawSegmentPart(
        segmentIndex: Int,
        localStart: Float,
        localEnd: Float,
        color: Color,
        strokeWidth: Float
    ) {
        if (localEnd <= localStart || strokeWidth <= 0f) return

        when (segmentIndex) {
            0 -> drawLine(
                color = color,
                start = Offset(left + r + localStart, top),
                end = Offset(left + r + localEnd, top),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Butt
            )
            1 -> if (r > 0f) {
                val startAngle = (-90f + (localStart / r) * 180f / PI.toFloat())
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(trCenter.x - r, trCenter.y - r),
                    size = Size(2f * r, 2f * r),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }
            2 -> drawLine(
                color = color,
                start = Offset(right, top + r + localStart),
                end = Offset(right, top + r + localEnd),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Butt
            )
            3 -> if (r > 0f) {
                val startAngle = (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(brCenter.x - r, brCenter.y - r),
                    size = Size(2f * r, 2f * r),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }
            4 -> drawLine(
                color = color,
                start = Offset(right - r - localStart, bottom),
                end = Offset(right - r - localEnd, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Butt
            )
            5 -> if (r > 0f) {
                val startAngle = 90f + (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(blCenter.x - r, blCenter.y - r),
                    size = Size(2f * r, 2f * r),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }
            6 -> drawLine(
                color = color,
                start = Offset(left, bottom - r - localStart),
                end = Offset(left, bottom - r - localEnd),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Butt
            )
            else -> if (r > 0f) {
                val startAngle = 180f + (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(tlCenter.x - r, tlCenter.y - r),
                    size = Size(2f * r, 2f * r),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }
        }
    }

    fun DrawScope.drawDistanceInterval(
        startDistance: Float,
        endDistance: Float,
        color: Color,
        strokeWidth: Float
    ) {
        if (endDistance <= startDistance) return
        for (segmentIndex in segmentStarts.indices) {
            val segmentStart = segmentStarts[segmentIndex]
            val segmentEnd = segmentStart + segmentLengths[segmentIndex]
            if (segmentEnd <= segmentStart) continue

            val overlapStart = max(startDistance, segmentStart)
            val overlapEnd = min(endDistance, segmentEnd)
            if (overlapEnd <= overlapStart) continue

            val localStart = overlapStart - segmentStart
            val localEnd = overlapEnd - segmentStart
            drawSegmentPart(
                segmentIndex = segmentIndex,
                localStart = localStart,
                localEnd = localEnd,
                color = color,
                strokeWidth = strokeWidth
            )
        }
    }

    fun drawSegmentPartNative(
        canvas: android.graphics.Canvas,
        segmentIndex: Int,
        localStart: Float,
        localEnd: Float,
        paint: Paint
    ) {
        if (localEnd <= localStart || paint.strokeWidth <= 0f) return
        when (segmentIndex) {
            0 -> canvas.drawLine(
                left + r + localStart,
                top,
                left + r + localEnd,
                top,
                paint
            )
            1 -> if (r > 0f) {
                val startAngle = (-90f + (localStart / r) * 180f / PI.toFloat())
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                canvas.drawArc(
                    RectF(trCenter.x - r, trCenter.y - r, trCenter.x + r, trCenter.y + r),
                    startAngle,
                    sweep,
                    false,
                    paint
                )
            }
            2 -> canvas.drawLine(
                right,
                top + r + localStart,
                right,
                top + r + localEnd,
                paint
            )
            3 -> if (r > 0f) {
                val startAngle = (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                canvas.drawArc(
                    RectF(brCenter.x - r, brCenter.y - r, brCenter.x + r, brCenter.y + r),
                    startAngle,
                    sweep,
                    false,
                    paint
                )
            }
            4 -> canvas.drawLine(
                right - r - localStart,
                bottom,
                right - r - localEnd,
                bottom,
                paint
            )
            5 -> if (r > 0f) {
                val startAngle = 90f + (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                canvas.drawArc(
                    RectF(blCenter.x - r, blCenter.y - r, blCenter.x + r, blCenter.y + r),
                    startAngle,
                    sweep,
                    false,
                    paint
                )
            }
            6 -> canvas.drawLine(
                left,
                bottom - r - localStart,
                left,
                bottom - r - localEnd,
                paint
            )
            else -> if (r > 0f) {
                val startAngle = 180f + (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                canvas.drawArc(
                    RectF(tlCenter.x - r, tlCenter.y - r, tlCenter.x + r, tlCenter.y + r),
                    startAngle,
                    sweep,
                    false,
                    paint
                )
            }
        }
    }

    fun drawDistanceIntervalNative(
        canvas: android.graphics.Canvas,
        startDistance: Float,
        endDistance: Float,
        paint: Paint
    ) {
        if (endDistance <= startDistance) return
        for (segmentIndex in segmentStarts.indices) {
            val segmentStart = segmentStarts[segmentIndex]
            val segmentEnd = segmentStart + segmentLengths[segmentIndex]
            if (segmentEnd <= segmentStart) continue

            val overlapStart = max(startDistance, segmentStart)
            val overlapEnd = min(endDistance, segmentEnd)
            if (overlapEnd <= overlapStart) continue

            val localStart = overlapStart - segmentStart
            val localEnd = overlapEnd - segmentStart
            drawSegmentPartNative(
                canvas = canvas,
                segmentIndex = segmentIndex,
                localStart = localStart,
                localEnd = localEnd,
                paint = paint
            )
        }
    }

    val normalizedProgress = ((progress % 1f) + 1f) % 1f
    val headDistance = normalizedProgress * totalLen
    val snakeLength = (snakeLengthFraction.coerceIn(0f, 1f) * totalLen).coerceAtLeast(0f)
    val head = pointAt(headDistance)
    val tailDistance = if (snakeLength >= totalLen) 0f else headDistance - snakeLength
    val glowStrokePaint = Paint().apply {
        isAntiAlias = true
        color = glowShadowColor.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = glowingStrokeWidthPx
        strokeCap = Paint.Cap.BUTT
        strokeJoin = Paint.Join.ROUND
        maskFilter = BlurMaskFilter(glowingBlurRadiusPx, BlurMaskFilter.Blur.NORMAL)
    }
    val glowHeadPaint = Paint().apply {
        isAntiAlias = true
        color = glowShadowColor.toArgb()
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(glowingBlurRadiusPx, BlurMaskFilter.Blur.NORMAL)
    }

    onDrawBehind {
        if (snakeLength > 0f) {
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                if (snakeLength >= totalLen) {
                    drawDistanceIntervalNative(
                        canvas = nativeCanvas,
                        startDistance = 0f,
                        endDistance = totalLen,
                        paint = glowStrokePaint
                    )
                } else if (tailDistance >= 0f) {
                    drawDistanceIntervalNative(
                        canvas = nativeCanvas,
                        startDistance = tailDistance,
                        endDistance = headDistance,
                        paint = glowStrokePaint
                    )
                } else {
                    drawDistanceIntervalNative(
                        canvas = nativeCanvas,
                        startDistance = tailDistance + totalLen,
                        endDistance = totalLen,
                        paint = glowStrokePaint
                    )
                    drawDistanceIntervalNative(
                        canvas = nativeCanvas,
                        startDistance = 0f,
                        endDistance = headDistance,
                        paint = glowStrokePaint
                    )
                }
                nativeCanvas.drawCircle(
                    head.x,
                    head.y,
                    glowingStrokeWidthPx / 2f,
                    glowHeadPaint
                )
            }

            if (snakeLength >= totalLen) {
                drawDistanceInterval(
                    startDistance = 0f,
                    endDistance = totalLen,
                    color = bodyColor,
                    strokeWidth = bodyStrokeWidthPx
                )
            } else {
                if (tailDistance >= 0f) {
                    drawDistanceInterval(
                        startDistance = tailDistance,
                        endDistance = headDistance,
                        color = bodyColor,
                        strokeWidth = bodyStrokeWidthPx
                    )
                } else {
                    drawDistanceInterval(
                        startDistance = tailDistance + totalLen,
                        endDistance = totalLen,
                        color = bodyColor,
                        strokeWidth = bodyStrokeWidthPx
                    )
                    drawDistanceInterval(
                        startDistance = 0f,
                        endDistance = headDistance,
                        color = bodyColor,
                        strokeWidth = bodyStrokeWidthPx
                    )
                }
            }

            // Head rounding is drawn as a separate circle. Tail remains sharp by design.
            drawCircle(
                color = bodyColor,
                radius = bodyStrokeWidthPx / 2f,
                center = head
            )
        }
    }
}
