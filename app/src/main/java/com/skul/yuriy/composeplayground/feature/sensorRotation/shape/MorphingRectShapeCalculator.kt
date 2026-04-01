package com.skul.yuriy.composeplayground.feature.sensorRotation.shape

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Treats the shape as a true rectangle at all times.
 *
 * The rectangle rotates around the container center by the current angle, while its width and
 * height interpolate toward the next quarter-turn dimensions. At 0 degrees the rectangle uses the
 * original width/height, at 90 degrees the dimensions are swapped, and the same pattern repeats
 * for each next 90-degree sector.
 */
class MorphingRectShapeCalculator : IRotationShapeCalculator {
    override fun calculate(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): ShapePoints {
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

        val currentWidth = lerp(startWidth, endWidth, quarterTurnProgress)
        val currentHeight = lerp(startHeight, endHeight, quarterTurnProgress)
        val center = Offset(
            x = (anchorA.x + anchorC.x) / 2f,
            y = (anchorA.y + anchorC.y) / 2f
        )

        val halfWidth = currentWidth / 2f
        val halfHeight = currentHeight / 2f

        return ShapePoints(
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
