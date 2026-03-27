package com.skul.yuriy.composeplayground.feature.animatedRectButton.snake

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
    state: RectSnakeState,
    cornerRadius: Dp = 28.dp,
    shape: Shape? = null,
    snakeLengthFraction: Float = 0.28f,
    bodyStrokeWidth: Dp = 2.dp,
    glowingShadowWidth: Dp = 12.dp,
    glowingBlurRadius: Dp = glowingShadowWidth / 2,
    trackPlacement: RectSnakeTrackPlacement = RectSnakeTrackPlacement.CENTER_ON_EDGE
): Modifier = this.drawWithCache {
    val resolvedShape = shape ?: RoundedCornerShape(cornerRadius)
    val geometry = buildRectSnakeTrackGeometry(
        size = size,
        bodyStrokeWidthPx = bodyStrokeWidth.toPx(),
        glowingStrokeWidthPx = glowingShadowWidth.toPx(),
        glowingBlurRadiusPx = glowingBlurRadius.toPx(),
        shape = resolvedShape,
        layoutDirection = layoutDirection,
        density = this,
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
        progress = state.progress,
        snakeLengthFraction = snakeLengthFraction,
        totalLen = totalLen
    )
    val bodyStrokePaint = createBodyStrokePaint(
        color = bodyColorTo,
        strokeWidthPx = bodyStrokeWidthPx
    )
    val glowStrokePaint = createGlowStrokePaint(
        color = glowColorTo,
        strokeWidthPx = glowingStrokeWidthPx,
        blurRadiusPx = glowingBlurRadiusPx
    )
    val glowHeadPaint = createGlowHeadPaint(
        color = glowColorTo,
        blurRadiusPx = glowingBlurRadiusPx
    )

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
    val topLeftRadius: Float,
    val topRightRadius: Float,
    val bottomRightRadius: Float,
    val bottomLeftRadius: Float,
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
    shape: Shape,
    layoutDirection: LayoutDirection,
    density: Density,
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

    val radii = resolveTrackCornerRadii(
        size = size,
        shape = shape,
        layoutDirection = layoutDirection,
        density = density,
        borderInset = borderInset,
        trackWidth = trackWidth,
        trackHeight = trackHeight
    )
    val topLen = (trackWidth - radii.topLeft - radii.topRight).coerceAtLeast(0f)
    val rightLen = (trackHeight - radii.topRight - radii.bottomRight).coerceAtLeast(0f)
    val bottomLen = (trackWidth - radii.bottomRight - radii.bottomLeft).coerceAtLeast(0f)
    val leftLen = (trackHeight - radii.bottomLeft - radii.topLeft).coerceAtLeast(0f)
    val topRightArcLen = (PI.toFloat() * radii.topRight / 2f).coerceAtLeast(0f)
    val bottomRightArcLen = (PI.toFloat() * radii.bottomRight / 2f).coerceAtLeast(0f)
    val bottomLeftArcLen = (PI.toFloat() * radii.bottomLeft / 2f).coerceAtLeast(0f)
    val topLeftArcLen = (PI.toFloat() * radii.topLeft / 2f).coerceAtLeast(0f)
    val segmentLengths = floatArrayOf(
        topLen,
        topRightArcLen,
        rightLen,
        bottomRightArcLen,
        bottomLen,
        bottomLeftArcLen,
        leftLen,
        topLeftArcLen
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
        topLeftRadius = radii.topLeft,
        topRightRadius = radii.topRight,
        bottomRightRadius = radii.bottomRight,
        bottomLeftRadius = radii.bottomLeft,
        segmentLengths = segmentLengths,
        segmentStarts = segmentStarts,
        totalLen = totalLen,
        topRightCenter = Offset(right - radii.topRight, top + radii.topRight),
        bottomRightCenter = Offset(right - radii.bottomRight, bottom - radii.bottomRight),
        bottomLeftCenter = Offset(left + radii.bottomLeft, bottom - radii.bottomLeft),
        topLeftCenter = Offset(left + radii.topLeft, top + radii.topLeft)
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
    return when (segmentAt(segmentIndex)) {
        RectSnakeSegment.TopEdge -> Offset(
            geometry.left + geometry.topLeftRadius + local,
            geometry.top
        )
        RectSnakeSegment.TopRightArc -> {
            val radius = geometry.topRightRadius
            if (radius <= 0f) {
                Offset(geometry.right, geometry.top)
            } else {
                val angle = (-PI / 2f + local / radius).toFloat()
                pointOnCircle(geometry.topRightCenter, radius, angle)
            }
        }
        RectSnakeSegment.RightEdge -> Offset(
            geometry.right,
            geometry.top + geometry.topRightRadius + local
        )
        RectSnakeSegment.BottomRightArc -> {
            val radius = geometry.bottomRightRadius
            if (radius <= 0f) {
                Offset(geometry.right, geometry.bottom)
            } else {
                val angle = local / radius
                pointOnCircle(geometry.bottomRightCenter, radius, angle)
            }
        }
        RectSnakeSegment.BottomEdge -> Offset(
            geometry.right - geometry.bottomRightRadius - local,
            geometry.bottom
        )
        RectSnakeSegment.BottomLeftArc -> {
            val radius = geometry.bottomLeftRadius
            if (radius <= 0f) {
                Offset(geometry.left, geometry.bottom)
            } else {
                val angle = ((PI / 2f) + local / radius).toFloat()
                pointOnCircle(geometry.bottomLeftCenter, radius, angle)
            }
        }
        RectSnakeSegment.LeftEdge -> Offset(
            geometry.left,
            geometry.bottom - geometry.bottomLeftRadius - local
        )
        RectSnakeSegment.TopLeftArc -> {
            val radius = geometry.topLeftRadius
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

private fun createBodyStrokePaint(
    color: Color,
    strokeWidthPx: Float
): Paint = Paint().apply {
    isAntiAlias = true
    this.color = color.toArgb()
    style = Paint.Style.STROKE
    strokeWidth = strokeWidthPx
    strokeCap = Paint.Cap.BUTT
    strokeJoin = Paint.Join.ROUND
}

private fun createGlowStrokePaint(
    color: Color,
    strokeWidthPx: Float,
    blurRadiusPx: Float
): Paint = Paint().apply {
    isAntiAlias = true
    this.color = color.toArgb()
    style = Paint.Style.STROKE
    strokeWidth = strokeWidthPx
    strokeCap = Paint.Cap.BUTT
    strokeJoin = Paint.Join.ROUND
    maskFilter = BlurMaskFilter(blurRadiusPx, BlurMaskFilter.Blur.NORMAL)
}

private fun createGlowHeadPaint(
    color: Color,
    blurRadiusPx: Float
): Paint = Paint().apply {
    isAntiAlias = true
    this.color = color.toArgb()
    style = Paint.Style.FILL
    maskFilter = BlurMaskFilter(blurRadiusPx, BlurMaskFilter.Blur.NORMAL)
}

private class RectSnakeCornerRadii(
    val topLeft: Float,
    val topRight: Float,
    val bottomRight: Float,
    val bottomLeft: Float
)

private fun resolveTrackCornerRadii(
    size: androidx.compose.ui.geometry.Size,
    shape: Shape,
    layoutDirection: LayoutDirection,
    density: Density,
    borderInset: Float,
    trackWidth: Float,
    trackHeight: Float,
): RectSnakeCornerRadii {
    val shapeRadii = when (shape) {
        is RoundedCornerShape -> {
            val outline = shape.createOutline(size, layoutDirection, density)
            when (outline) {
                is Outline.Rounded -> RectSnakeCornerRadii(
                    topLeft = outline.roundRect.topLeftCornerRadius.x,
                    topRight = outline.roundRect.topRightCornerRadius.x,
                    bottomRight = outline.roundRect.bottomRightCornerRadius.x,
                    bottomLeft = outline.roundRect.bottomLeftCornerRadius.x
                )
                else -> RectSnakeCornerRadii(0f, 0f, 0f, 0f)
            }
        }
        RectangleShape -> RectSnakeCornerRadii(0f, 0f, 0f, 0f)
        CircleShape -> {
            val radius = min(size.width, size.height) / 2f
            RectSnakeCornerRadii(radius, radius, radius, radius)
        }
        else -> error(
            "rectSnakeBorder supports only RoundedCornerShape, RectangleShape, and CircleShape"
        )
    }
    return normalizeCornerRadii(
        topLeft = (shapeRadii.topLeft - borderInset).coerceAtLeast(0f),
        topRight = (shapeRadii.topRight - borderInset).coerceAtLeast(0f),
        bottomRight = (shapeRadii.bottomRight - borderInset).coerceAtLeast(0f),
        bottomLeft = (shapeRadii.bottomLeft - borderInset).coerceAtLeast(0f),
        width = trackWidth,
        height = trackHeight
    )
}

private fun normalizeCornerRadii(
    topLeft: Float,
    topRight: Float,
    bottomRight: Float,
    bottomLeft: Float,
    width: Float,
    height: Float,
): RectSnakeCornerRadii {
    val widthTopScale = scaleToFit(width, topLeft + topRight)
    val widthBottomScale = scaleToFit(width, bottomLeft + bottomRight)
    val heightLeftScale = scaleToFit(height, topLeft + bottomLeft)
    val heightRightScale = scaleToFit(height, topRight + bottomRight)
    val scale = min(min(widthTopScale, widthBottomScale), min(heightLeftScale, heightRightScale))
    return RectSnakeCornerRadii(
        topLeft = topLeft * scale,
        topRight = topRight * scale,
        bottomRight = bottomRight * scale,
        bottomLeft = bottomLeft * scale
    )
}

private fun scaleToFit(limit: Float, sum: Float): Float {
    if (sum <= 0f || sum <= limit) return 1f
    return limit / sum
}

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
    when (segment) {
        RectSnakeSegment.TopEdge -> {
            val x0 = left + geometry.topLeftRadius + localStart
            val y0 = top
            val x1 = left + geometry.topLeftRadius + localEnd
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
        RectSnakeSegment.TopRightArc -> if (geometry.topRightRadius > 0f) {
            val radius = geometry.topRightRadius
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
            val y0 = top + geometry.topRightRadius + localStart
            val x1 = right
            val y1 = top + geometry.topRightRadius + localEnd
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
        RectSnakeSegment.BottomRightArc -> if (geometry.bottomRightRadius > 0f) {
            val radius = geometry.bottomRightRadius
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
            val x0 = right - geometry.bottomRightRadius - localStart
            val y0 = bottom
            val x1 = right - geometry.bottomRightRadius - localEnd
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
        RectSnakeSegment.BottomLeftArc -> if (geometry.bottomLeftRadius > 0f) {
            val radius = geometry.bottomLeftRadius
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
            val y0 = bottom - geometry.bottomLeftRadius - localStart
            val x1 = left
            val y1 = bottom - geometry.bottomLeftRadius - localEnd
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
        RectSnakeSegment.TopLeftArc -> if (geometry.topLeftRadius > 0f) {
            val radius = geometry.topLeftRadius
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
