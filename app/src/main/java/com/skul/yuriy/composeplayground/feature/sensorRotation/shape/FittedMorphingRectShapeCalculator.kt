package com.skul.yuriy.composeplayground.feature.sensorRotation.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * A rectangle-based calculator that always keeps the shape fully visible inside the source bounds.
 *
 * The algorithm works in two steps:
 * 1. It builds the same rotating rectangle as {@link MorphingRectShapeCalculator}: the shape keeps
 *    90-degree corners, rotates around the center, and linearly swaps width/height over every
 *    90-degree sector.
 * 2. It computes the bounding box of that rotated rectangle and applies a uniform scale so the
 *    full shape still fits inside the original container. This makes the rectangle shrink while it
 *    approaches the "widest" intermediate angles and grow back to full size at canonical
 *    positions such as 0, 90, 180 and -90 degrees.
 */
class FittedMorphingRectShapeCalculator : IRotationShapeCalculator {
    override fun calculate(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): RotationShapeLayoutData {
        val normalizedAngle = normalizeToSignedHalfTurn(rotationDegrees)
        val absoluteAngle = abs(normalizedAngle)
        val completedQuarterTurns = (absoluteAngle / QUARTER_TURN_DEGREES).toInt()
        val quarterTurnProgress = (absoluteAngle % QUARTER_TURN_DEGREES) / QUARTER_TURN_DEGREES

        val baseWidth = distance(anchorA, anchorB)
        val baseHeight = distance(anchorB, anchorC)
        val startWidth = if (completedQuarterTurns % 2 == 0) baseWidth else baseHeight
        val startHeight = if (completedQuarterTurns % 2 == 0) baseHeight else baseWidth
        val endWidth = if (completedQuarterTurns % 2 == 0) baseHeight else baseWidth
        val endHeight = if (completedQuarterTurns % 2 == 0) baseWidth else baseHeight

        val morphWidth = lerp(startWidth, endWidth, quarterTurnProgress)
        val morphHeight = lerp(startHeight, endHeight, quarterTurnProgress)
        val fitScale = resolveFitScale(
            containerWidth = baseWidth,
            containerHeight = baseHeight,
            rectWidth = morphWidth,
            rectHeight = morphHeight,
            rotationDegrees = normalizedAngle
        )

        val currentWidth = morphWidth * fitScale
        val currentHeight = morphHeight * fitScale
        val center = Offset(
            x = (anchorA.x + anchorC.x) / 2f,
            y = (anchorA.y + anchorC.y) / 2f
        )

        val halfWidth = currentWidth / 2f
        val halfHeight = currentHeight / 2f

        val shapePoints = ShapePoints(
            a1 = rotateLocalPoint(
                localPoint = Offset(-halfWidth, -halfHeight),
                center = center,
                rotationDegrees = normalizedAngle
            ),
            b1 = rotateLocalPoint(
                localPoint = Offset(halfWidth, -halfHeight),
                center = center,
                rotationDegrees = normalizedAngle
            ),
            c1 = rotateLocalPoint(
                localPoint = Offset(halfWidth, halfHeight),
                center = center,
                rotationDegrees = normalizedAngle
            ),
            d1 = rotateLocalPoint(
                localPoint = Offset(-halfWidth, halfHeight),
                center = center,
                rotationDegrees = normalizedAngle
            )
        )
        return RotationShapeLayoutData.fromShapePoints(
            shapePoints = shapePoints,
            contentSize = Size(width = currentWidth, height = currentHeight),
        )
    }

    private fun resolveFitScale(
        containerWidth: Float,
        containerHeight: Float,
        rectWidth: Float,
        rectHeight: Float,
        rotationDegrees: Float
    ): Float {
        val radians = Math.toRadians(rotationDegrees.toDouble())
        val absCos = abs(cos(radians)).toFloat()
        val absSin = abs(sin(radians)).toFloat()

        val rotatedWidth = rectWidth * absCos + rectHeight * absSin
        val rotatedHeight = rectWidth * absSin + rectHeight * absCos
        val scaleX = containerWidth / rotatedWidth
        val scaleY = containerHeight / rotatedHeight
        return minOf(scaleX, scaleY, 1f)
    }

    private fun rotateLocalPoint(
        localPoint: Offset,
        center: Offset,
        rotationDegrees: Float
    ): Offset {
        val radians = Math.toRadians(rotationDegrees.toDouble())
        val cosValue = cos(radians).toFloat()
        val sinValue = sin(radians).toFloat()

        return Offset(
            x = center.x + localPoint.x * cosValue - localPoint.y * sinValue,
            y = center.y + localPoint.x * sinValue + localPoint.y * cosValue
        )
    }

    private fun distance(start: Offset, end: Offset): Float {
        val dx = end.x - start.x
        val dy = end.y - start.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float {
        val clampedProgress = progress.coerceIn(0f, 1f)
        return start + (end - start) * clampedProgress
    }

    private fun normalizeToSignedHalfTurn(angle: Float): Float {
        val normalized = angle % FULL_TURN_DEGREES
        return when {
            normalized > HALF_TURN_DEGREES -> normalized - FULL_TURN_DEGREES
            normalized <= -HALF_TURN_DEGREES -> normalized + FULL_TURN_DEGREES
            else -> normalized
        }
    }

    private companion object {
        const val QUARTER_TURN_DEGREES = 90f
        const val HALF_TURN_DEGREES = 180f
        const val FULL_TURN_DEGREES = 360f
    }
}
