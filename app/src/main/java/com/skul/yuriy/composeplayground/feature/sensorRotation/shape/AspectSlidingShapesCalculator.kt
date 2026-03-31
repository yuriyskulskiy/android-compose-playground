package com.skul.yuriy.composeplayground.feature.sensorRotation.shape

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Keeps all four points moving at the same time.
 *
 * The current angle inside each 90-degree sector is converted into a normalized progress using
 * the container aspect ratio and `tan(angle)`. That progress is then applied to all four corners:
 * clockwise sectors slide `A1->B`, `B1->C`, `C1->D`, `D1->A`, and counter-clockwise sectors slide
 * in the opposite direction. Because the same progress is used on all four sides, the top and
 * bottom shelves remain parallel to the debug horizon line.
 */
class AspectSlidingShapesCalculator : IRotationShapeCalculator {
    override fun calculate(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): ShapePoints {
        val normalizedAngle = normalizeToSignedHalfTurn(rotationDegrees)
        val isClockwise = normalizedAngle >= 0f
        val absoluteAngle = abs(normalizedAngle)
        val completedQuarterTurns = (absoluteAngle / QUARTER_TURN_DEGREES).toInt()
        val localRotationDegrees = absoluteAngle % QUARTER_TURN_DEGREES
        val anchors = listOf(anchorA, anchorB, anchorC, anchorD)

        val localAnchorA = anchors[rotateIndex(0, completedQuarterTurns, isClockwise)]
        val localAnchorB = anchors[rotateIndex(1, completedQuarterTurns, isClockwise)]
        val localAnchorC = anchors[rotateIndex(2, completedQuarterTurns, isClockwise)]
        val localAnchorD = anchors[rotateIndex(3, completedQuarterTurns, isClockwise)]

        return if (isClockwise) {
            resolveClockwiseSectorShapePoints(
                anchorA = localAnchorA,
                anchorB = localAnchorB,
                anchorC = localAnchorC,
                anchorD = localAnchorD,
                localRotationDegrees = localRotationDegrees
            )
        } else {
            resolveCounterClockwiseSectorShapePoints(
                anchorA = localAnchorA,
                anchorB = localAnchorB,
                anchorC = localAnchorC,
                anchorD = localAnchorD,
                localRotationDegrees = localRotationDegrees
            )
        }
    }

    private fun resolveClockwiseSectorShapePoints(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        localRotationDegrees: Float
    ): ShapePoints {
        val progress = resolveSmoothProgress(
            horizontalLength = distance(anchorA, anchorB),
            verticalLength = distance(anchorB, anchorC),
            localRotationDegrees = localRotationDegrees
        )

        return ShapePoints(
            a1 = lerp(anchorA, anchorB, progress),
            b1 = lerp(anchorB, anchorC, progress),
            c1 = lerp(anchorC, anchorD, progress),
            d1 = lerp(anchorD, anchorA, progress)
        )
    }

    private fun resolveCounterClockwiseSectorShapePoints(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        localRotationDegrees: Float
    ): ShapePoints {
        val progress = resolveSmoothProgress(
            horizontalLength = distance(anchorA, anchorB),
            verticalLength = distance(anchorB, anchorC),
            localRotationDegrees = localRotationDegrees
        )

        return ShapePoints(
            a1 = lerp(anchorA, anchorD, progress),
            b1 = lerp(anchorB, anchorA, progress),
            c1 = lerp(anchorC, anchorB, progress),
            d1 = lerp(anchorD, anchorC, progress)
        )
    }

    private fun resolveSmoothProgress(
        horizontalLength: Float,
        verticalLength: Float,
        localRotationDegrees: Float
    ): Float {
        if (localRotationDegrees <= 0f) return 0f
        if (localRotationDegrees >= QUARTER_TURN_DEGREES) return 1f

        val tangent = tan(Math.toRadians(localRotationDegrees.toDouble())).toFloat()
        val aspectRatio = horizontalLength / verticalLength
        return (aspectRatio * tangent) / (1f + aspectRatio * tangent)
    }

    private fun rotateIndex(index: Int, steps: Int, clockwise: Boolean): Int {
        val direction = if (clockwise) 1 else -1
        return ((index + direction * steps) % ANCHOR_COUNT + ANCHOR_COUNT) % ANCHOR_COUNT
    }

    private fun lerp(start: Offset, end: Offset, progress: Float): Offset {
        val clampedProgress = progress.coerceIn(0f, 1f)
        return Offset(
            x = start.x + (end.x - start.x) * clampedProgress,
            y = start.y + (end.y - start.y) * clampedProgress
        )
    }

    private fun distance(start: Offset, end: Offset): Float {
        val dx = end.x - start.x
        val dy = end.y - start.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
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
        const val ANCHOR_COUNT = 4
        const val QUARTER_TURN_DEGREES = 90f
        const val HALF_TURN_DEGREES = 180f
        const val FULL_TURN_DEGREES = 360f
    }
}
