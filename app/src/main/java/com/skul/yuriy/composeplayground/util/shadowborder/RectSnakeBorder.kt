package com.skul.yuriy.composeplayground.util.shadowborder

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
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
import kotlin.math.ceil
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

    fun colorLerp(start: Color, end: Color, fraction: Float): Color {
        val t = fraction.coerceIn(0f, 1f)
        return Color(
            red = start.red + (end.red - start.red) * t,
            green = start.green + (end.green - start.green) * t,
            blue = start.blue + (end.blue - start.blue) * t,
            alpha = start.alpha + (end.alpha - start.alpha) * t
        )
    }

    fun pointOnArc(center: Offset, angleDegrees: Float): Offset {
        val angleRad = Math.toRadians(angleDegrees.toDouble())
        return Offset(
            x = center.x + r * cos(angleRad).toFloat(),
            y = center.y + r * sin(angleRad).toFloat()
        )
    }

    fun DrawScope.drawArcSegmentGradient(
        center: Offset,
        startAngleDegrees: Float,
        sweepDegrees: Float,
        startColor: Color,
        endColor: Color,
        strokeWidth: Float
    ) {
        if (r <= 0f || sweepDegrees <= 0f || strokeWidth <= 0f) return
        val steps = max(1, ceil((sweepDegrees / 10f).toDouble()).toInt())
        for (step in 0 until steps) {
            val t0 = step / steps.toFloat()
            val t1 = (step + 1) / steps.toFloat()
            val angle0 = startAngleDegrees + sweepDegrees * t0
            val angle1 = startAngleDegrees + sweepDegrees * t1
            val p0 = pointOnArc(center, angle0)
            val p1 = pointOnArc(center, angle1)
            val c0 = colorLerp(startColor, endColor, t0)
            val c1 = colorLerp(startColor, endColor, t1)
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(c0, c1),
                    start = p0,
                    end = p1
                ),
                start = p0,
                end = p1,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Butt
            )
        }
    }

    fun drawArcSegmentGradientNative(
        canvas: android.graphics.Canvas,
        center: Offset,
        startAngleDegrees: Float,
        sweepDegrees: Float,
        startColor: Color,
        endColor: Color,
        paint: Paint
    ) {
        if (r <= 0f || sweepDegrees <= 0f || paint.strokeWidth <= 0f) return
        val steps = max(1, ceil((sweepDegrees / 10f).toDouble()).toInt())
        for (step in 0 until steps) {
            val t0 = step / steps.toFloat()
            val t1 = (step + 1) / steps.toFloat()
            val angle0 = startAngleDegrees + sweepDegrees * t0
            val angle1 = startAngleDegrees + sweepDegrees * t1
            val p0 = pointOnArc(center, angle0)
            val p1 = pointOnArc(center, angle1)
            val c0 = colorLerp(startColor, endColor, t0)
            val c1 = colorLerp(startColor, endColor, t1)
            paint.shader = LinearGradient(
                p0.x,
                p0.y,
                p1.x,
                p1.y,
                c0.toArgb(),
                c1.toArgb(),
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(p0.x, p0.y, p1.x, p1.y, paint)
        }
    }

    fun DrawScope.drawSegmentPart(
        segmentIndex: Int,
        localStart: Float,
        localEnd: Float,
        startColor: Color,
        endColor: Color,
        strokeWidth: Float
    ) {
        if (localEnd <= localStart || strokeWidth <= 0f) return

        when (segmentIndex) {
            0 -> {
                val start = Offset(left + r + localStart, top)
                val end = Offset(left + r + localEnd, top)
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(startColor, endColor),
                        start = start,
                        end = end
                    ),
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Butt
                )
            }
            1 -> if (r > 0f) {
                val startAngle = (-90f + (localStart / r) * 180f / PI.toFloat())
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArcSegmentGradient(
                    center = trCenter,
                    startAngleDegrees = startAngle,
                    sweepDegrees = sweep,
                    startColor = startColor,
                    endColor = endColor,
                    strokeWidth = strokeWidth
                )
            }
            2 -> {
                val start = Offset(right, top + r + localStart)
                val end = Offset(right, top + r + localEnd)
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(startColor, endColor),
                        start = start,
                        end = end
                    ),
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Butt
                )
            }
            3 -> if (r > 0f) {
                val startAngle = (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArcSegmentGradient(
                    center = brCenter,
                    startAngleDegrees = startAngle,
                    sweepDegrees = sweep,
                    startColor = startColor,
                    endColor = endColor,
                    strokeWidth = strokeWidth
                )
            }
            4 -> {
                val start = Offset(right - r - localStart, bottom)
                val end = Offset(right - r - localEnd, bottom)
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(startColor, endColor),
                        start = start,
                        end = end
                    ),
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Butt
                )
            }
            5 -> if (r > 0f) {
                val startAngle = 90f + (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArcSegmentGradient(
                    center = blCenter,
                    startAngleDegrees = startAngle,
                    sweepDegrees = sweep,
                    startColor = startColor,
                    endColor = endColor,
                    strokeWidth = strokeWidth
                )
            }
            6 -> {
                val start = Offset(left, bottom - r - localStart)
                val end = Offset(left, bottom - r - localEnd)
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(startColor, endColor),
                        start = start,
                        end = end
                    ),
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Butt
                )
            }
            else -> if (r > 0f) {
                val startAngle = 180f + (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArcSegmentGradient(
                    center = tlCenter,
                    startAngleDegrees = startAngle,
                    sweepDegrees = sweep,
                    startColor = startColor,
                    endColor = endColor,
                    strokeWidth = strokeWidth
                )
            }
        }
    }

    fun DrawScope.drawDistanceInterval(
        startDistance: Float,
        endDistance: Float,
        baseColor: Color,
        strokeWidth: Float,
        alphaAtDistance: (Float) -> Float
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
            val startColor = baseColor.copy(alpha = baseColor.alpha * alphaAtDistance(overlapStart))
            val endColor = baseColor.copy(alpha = baseColor.alpha * alphaAtDistance(overlapEnd))
            drawSegmentPart(
                segmentIndex = segmentIndex,
                localStart = localStart,
                localEnd = localEnd,
                startColor = startColor,
                endColor = endColor,
                strokeWidth = strokeWidth
            )
        }
    }

    fun drawSegmentPartNative(
        canvas: android.graphics.Canvas,
        segmentIndex: Int,
        localStart: Float,
        localEnd: Float,
        startColor: Color,
        endColor: Color,
        paint: Paint
    ) {
        if (localEnd <= localStart || paint.strokeWidth <= 0f) return
        when (segmentIndex) {
            0 -> {
                val x0 = left + r + localStart
                val y0 = top
                val x1 = left + r + localEnd
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
            1 -> if (r > 0f) {
                val startAngle = (-90f + (localStart / r) * 180f / PI.toFloat())
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArcSegmentGradientNative(
                    canvas = canvas,
                    center = trCenter,
                    startAngleDegrees = startAngle,
                    sweepDegrees = sweep,
                    startColor = startColor,
                    endColor = endColor,
                    paint = paint
                )
            }
            2 -> {
                val x0 = right
                val y0 = top + r + localStart
                val x1 = right
                val y1 = top + r + localEnd
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
            3 -> if (r > 0f) {
                val startAngle = (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArcSegmentGradientNative(
                    canvas = canvas,
                    center = brCenter,
                    startAngleDegrees = startAngle,
                    sweepDegrees = sweep,
                    startColor = startColor,
                    endColor = endColor,
                    paint = paint
                )
            }
            4 -> {
                val x0 = right - r - localStart
                val y0 = bottom
                val x1 = right - r - localEnd
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
            5 -> if (r > 0f) {
                val startAngle = 90f + (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArcSegmentGradientNative(
                    canvas = canvas,
                    center = blCenter,
                    startAngleDegrees = startAngle,
                    sweepDegrees = sweep,
                    startColor = startColor,
                    endColor = endColor,
                    paint = paint
                )
            }
            6 -> {
                val x0 = left
                val y0 = bottom - r - localStart
                val x1 = left
                val y1 = bottom - r - localEnd
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
            else -> if (r > 0f) {
                val startAngle = 180f + (localStart / r) * 180f / PI.toFloat()
                val sweep = ((localEnd - localStart) / r) * 180f / PI.toFloat()
                drawArcSegmentGradientNative(
                    canvas = canvas,
                    center = tlCenter,
                    startAngleDegrees = startAngle,
                    sweepDegrees = sweep,
                    startColor = startColor,
                    endColor = endColor,
                    paint = paint
                )
            }
        }
    }

    fun drawDistanceIntervalNative(
        canvas: android.graphics.Canvas,
        startDistance: Float,
        endDistance: Float,
        baseColor: Color,
        paint: Paint,
        alphaAtDistance: (Float) -> Float
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
            val startColor = baseColor.copy(alpha = baseColor.alpha * alphaAtDistance(overlapStart))
            val endColor = baseColor.copy(alpha = baseColor.alpha * alphaAtDistance(overlapEnd))
            drawSegmentPartNative(
                canvas = canvas,
                segmentIndex = segmentIndex,
                localStart = localStart,
                localEnd = localEnd,
                startColor = startColor,
                endColor = endColor,
                paint = paint
            )
        }
    }

    val normalizedProgress = ((progress % 1f) + 1f) % 1f
    val headDistance = normalizedProgress * totalLen
    val snakeLength = (snakeLengthFraction.coerceIn(0f, 1f) * totalLen).coerceAtLeast(0f)
    val head = pointAt(headDistance)
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
                        baseColor = glowShadowColor,
                        paint = glowStrokePaint
                        ,
                        alphaAtDistance = alphaAtDistance
                    )
                } else if (tailDistance >= 0f) {
                    drawDistanceIntervalNative(
                        canvas = nativeCanvas,
                        startDistance = tailDistance,
                        endDistance = headDistance,
                        baseColor = glowShadowColor,
                        paint = glowStrokePaint,
                        alphaAtDistance = alphaAtDistance
                    )
                } else {
                    drawDistanceIntervalNative(
                        canvas = nativeCanvas,
                        startDistance = tailDistance + totalLen,
                        endDistance = totalLen,
                        baseColor = glowShadowColor,
                        paint = glowStrokePaint,
                        alphaAtDistance = alphaAtDistance
                    )
                    drawDistanceIntervalNative(
                        canvas = nativeCanvas,
                        startDistance = 0f,
                        endDistance = headDistance,
                        baseColor = glowShadowColor,
                        paint = glowStrokePaint,
                        alphaAtDistance = alphaAtDistance
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
                    baseColor = bodyColor,
                    strokeWidth = bodyStrokeWidthPx,
                    alphaAtDistance = alphaAtDistance
                )
            } else {
                if (tailDistance >= 0f) {
                    drawDistanceInterval(
                        startDistance = tailDistance,
                        endDistance = headDistance,
                        baseColor = bodyColor,
                        strokeWidth = bodyStrokeWidthPx,
                        alphaAtDistance = alphaAtDistance
                    )
                } else {
                    drawDistanceInterval(
                        startDistance = tailDistance + totalLen,
                        endDistance = totalLen,
                        baseColor = bodyColor,
                        strokeWidth = bodyStrokeWidthPx,
                        alphaAtDistance = alphaAtDistance
                    )
                    drawDistanceInterval(
                        startDistance = 0f,
                        endDistance = headDistance,
                        baseColor = bodyColor,
                        strokeWidth = bodyStrokeWidthPx,
                        alphaAtDistance = alphaAtDistance
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
