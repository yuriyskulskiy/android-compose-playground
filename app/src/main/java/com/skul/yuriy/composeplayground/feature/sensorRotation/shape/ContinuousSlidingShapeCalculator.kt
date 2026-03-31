package com.skul.yuriy.composeplayground.feature.sensorRotation.shape

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

class ContinuousSlidingShapeCalculator : RotationShapeCalculatorContract {
    override fun calculate(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): ShapePoints {
        val anchors = listOf(anchorA, anchorB, anchorC, anchorD)
        val normalizedAngle = normalizeToSignedHalfTurn(rotationDegrees)
        val isPositiveRotation = normalizedAngle >= 0f
        val absoluteAngle = abs(normalizedAngle)
        val completedQuarterTurns = (absoluteAngle / QUARTER_TURN_DEGREES).toInt()
        val quarterTurnProgress =
            (absoluteAngle % QUARTER_TURN_DEGREES) / QUARTER_TURN_DEGREES

        fun pointFor(baseIndex: Int): Offset {
            val startIndex = rotateIndex(
                index = baseIndex,
                steps = completedQuarterTurns,
                clockwise = isPositiveRotation
            )
            val endIndex = rotateIndex(
                index = startIndex,
                steps = 1,
                clockwise = isPositiveRotation
            )
            return lerp(
                start = anchors[startIndex],
                end = anchors[endIndex],
                progress = quarterTurnProgress
            )
        }

        return ShapePoints(
            a1 = pointFor(0),
            b1 = pointFor(1),
            c1 = pointFor(2),
            d1 = pointFor(3)
        )
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
