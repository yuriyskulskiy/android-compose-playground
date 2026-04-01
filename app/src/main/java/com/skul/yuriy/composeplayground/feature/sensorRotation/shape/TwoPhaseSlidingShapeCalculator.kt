package com.skul.yuriy.composeplayground.feature.sensorRotation.shape

import android.util.Log
import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin

/**
 * Two-phase shape calculation used by the original debug model.
 *
 * For the first half of each 90-degree sector one point in the top pair and one point in the
 * bottom pair stay anchored to the corner that is closest to the world horizon, while the paired
 * points are resolved from ray/segment intersections so the shelves stay parallel to the debug
 * horizon. After 45 degrees the algorithm enters the second phase: the previously anchored points
 * also start sliding toward the next corners, while their paired points continue to be recomputed
 * from the same geometric constraints. Angles outside [-90, 90] are mirrored from the working
 * base sector to preserve the same two-phase behavior across the full rotation.
 */
class TwoPhaseSlidingShapeCalculator : IRotationShapeCalculator {
    override fun calculate(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): RotationShapeLayoutData {
        val shapePoints = when {
            rotationDegrees < -90f -> {
                val mirroredShapePoints = resolveBaseShapePoints(
                    anchorA = anchorA,
                    anchorB = anchorB,
                    anchorC = anchorC,
                    anchorD = anchorD,
                    rotationDegrees = -180f - rotationDegrees
                )
                val centerY = (anchorA.y + anchorD.y) / 2f
                ShapePoints(
                    a1 = mirrorHorizontally(mirroredShapePoints.b1, centerY),
                    b1 = mirrorHorizontally(mirroredShapePoints.a1, centerY),
                    c1 = mirrorHorizontally(mirroredShapePoints.d1, centerY),
                    d1 = mirrorHorizontally(mirroredShapePoints.c1, centerY)
                )
            }

            rotationDegrees <= 90f -> {
                resolveBaseShapePoints(
                    anchorA = anchorA,
                    anchorB = anchorB,
                    anchorC = anchorC,
                    anchorD = anchorD,
                    rotationDegrees = rotationDegrees
                )
            }

            else -> {
                val mirroredShapePoints = resolveBaseShapePoints(
                    anchorA = anchorA,
                    anchorB = anchorB,
                    anchorC = anchorC,
                    anchorD = anchorD,
                    rotationDegrees = 180f - rotationDegrees
                )
                val centerX = (anchorA.x + anchorB.x) / 2f
                ShapePoints(
                    a1 = mirrorVertically(mirroredShapePoints.b1, centerX),
                    b1 = mirrorVertically(mirroredShapePoints.a1, centerX),
                    c1 = mirrorVertically(mirroredShapePoints.d1, centerX),
                    d1 = mirrorVertically(mirroredShapePoints.c1, centerX)
                )
            }
        }
        return RotationShapeLayoutData.fromShapePoints(shapePoints)
    }

    private fun resolveBaseShapePoints(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): ShapePoints {
        val topPair = resolveTopPair(
            anchorA = anchorA,
            anchorB = anchorB,
            anchorC = anchorC,
            anchorD = anchorD,
            rotationDegrees = rotationDegrees
        )
        val bottomPair = resolveBottomPair(
            anchorA = anchorA,
            anchorB = anchorB,
            anchorC = anchorC,
            anchorD = anchorD,
            rotationDegrees = rotationDegrees
        )
        return ShapePoints(
            a1 = topPair.a1,
            b1 = topPair.b1,
            c1 = bottomPair.c1,
            d1 = bottomPair.d1
        )
    }

    private fun resolveTopPair(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): TopPair {
        val radians = Math.toRadians(rotationDegrees.toDouble())
        val gravityDown = Offset(
            x = (-sin(radians)).toFloat(),
            y = cos(radians).toFloat()
        )
        val horizontalRight = Offset(
            x = cos(radians).toFloat(),
            y = sin(radians).toFloat()
        )
        val fixedPointShiftProgress = resolveFixedPointShiftProgress(rotationDegrees)

        val bLowerThanA = dot(anchorB, gravityDown) > dot(anchorA, gravityDown)
        return if (bLowerThanA) {
            val (a1, b1) = if (fixedPointShiftProgress == 0f) {
                val movedA1 = intersectRayWithSegment(
                    rayOrigin = anchorB,
                    rayDirection = horizontalRight * -1f,
                    segmentStart = anchorA,
                    segmentEnd = anchorD
                )
                movedA1 to anchorB
            } else {
                val boundaryHorizontalRight = horizontalRightAtBoundary(rotationDegrees)
                val boundaryA1 = intersectRayWithSegment(
                    rayOrigin = anchorB,
                    rayDirection = boundaryHorizontalRight * -1f,
                    segmentStart = anchorA,
                    segmentEnd = anchorD
                )
                val movedA1 = lerp(boundaryA1, anchorD, fixedPointShiftProgress)
                val dependentB1 = intersectRayWithSegment(
                    rayOrigin = movedA1,
                    rayDirection = horizontalRight,
                    segmentStart = anchorA,
                    segmentEnd = anchorB
                )
                movedA1 to dependentB1
            }
            Log.d(
                "SensorRotationTopPair",
                "B lower by gravity: fixed=B1, moved=A1, shiftProgress=$fixedPointShiftProgress"
            )
            TopPair(
                a1 = a1,
                b1 = b1
            )
        } else {
            val (a1, b1) = if (fixedPointShiftProgress == 0f) {
                val movedB1 = intersectRayWithSegment(
                    rayOrigin = anchorA,
                    rayDirection = horizontalRight,
                    segmentStart = anchorB,
                    segmentEnd = anchorC
                )
                anchorA to movedB1
            } else {
                val boundaryHorizontalRight = horizontalRightAtBoundary(rotationDegrees)
                val boundaryB1 = intersectRayWithSegment(
                    rayOrigin = anchorA,
                    rayDirection = boundaryHorizontalRight,
                    segmentStart = anchorB,
                    segmentEnd = anchorC
                )
                val movedB1 = lerp(boundaryB1, anchorC, fixedPointShiftProgress)
                val dependentA1 = intersectRayWithSegment(
                    rayOrigin = movedB1,
                    rayDirection = horizontalRight * -1f,
                    segmentStart = anchorA,
                    segmentEnd = anchorB
                )
                dependentA1 to movedB1
            }
            Log.d(
                "SensorRotationTopPair",
                "A lower by gravity: fixed=A1, moved=B1, shiftProgress=$fixedPointShiftProgress"
            )
            TopPair(
                a1 = a1,
                b1 = b1
            )
        }
    }

    private fun resolveBottomPair(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): BottomPair {
        val radians = Math.toRadians(rotationDegrees.toDouble())
        val gravityDown = Offset(
            x = (-sin(radians)).toFloat(),
            y = cos(radians).toFloat()
        )
        val horizontalRight = Offset(
            x = cos(radians).toFloat(),
            y = sin(radians).toFloat()
        )
        val fixedPointShiftProgress = resolveFixedPointShiftProgress(rotationDegrees)

        val cHigherThanD = dot(anchorC, gravityDown) < dot(anchorD, gravityDown)
        return if (cHigherThanD) {
            val (c1, d1) = if (fixedPointShiftProgress == 0f) {
                val movedD1 = intersectRayWithSegment(
                    rayOrigin = anchorC,
                    rayDirection = horizontalRight * -1f,
                    segmentStart = anchorA,
                    segmentEnd = anchorD
                )
                anchorC to movedD1
            } else {
                val boundaryHorizontalRight = horizontalRightAtBoundary(rotationDegrees)
                val boundaryD1 = intersectRayWithSegment(
                    rayOrigin = anchorC,
                    rayDirection = boundaryHorizontalRight * -1f,
                    segmentStart = anchorA,
                    segmentEnd = anchorD
                )
                val movedD1 = lerp(boundaryD1, anchorA, fixedPointShiftProgress)
                val dependentC1 = intersectRayWithSegment(
                    rayOrigin = movedD1,
                    rayDirection = horizontalRight,
                    segmentStart = anchorC,
                    segmentEnd = anchorD
                )
                dependentC1 to movedD1
            }
            Log.d(
                "SensorRotationBottomPair",
                "C higher by gravity: fixed=C1, moved=D1, shiftProgress=$fixedPointShiftProgress"
            )
            BottomPair(
                c1 = c1,
                d1 = d1
            )
        } else {
            val (c1, d1) = if (fixedPointShiftProgress == 0f) {
                val movedC1 = intersectRayWithSegment(
                    rayOrigin = anchorD,
                    rayDirection = horizontalRight,
                    segmentStart = anchorB,
                    segmentEnd = anchorC
                )
                movedC1 to anchorD
            } else {
                val boundaryHorizontalRight = horizontalRightAtBoundary(rotationDegrees)
                val boundaryC1 = intersectRayWithSegment(
                    rayOrigin = anchorD,
                    rayDirection = boundaryHorizontalRight,
                    segmentStart = anchorB,
                    segmentEnd = anchorC
                )
                val movedC1 = lerp(boundaryC1, anchorB, fixedPointShiftProgress)
                val dependentD1 = intersectRayWithSegment(
                    rayOrigin = movedC1,
                    rayDirection = horizontalRight * -1f,
                    segmentStart = anchorC,
                    segmentEnd = anchorD
                )
                movedC1 to dependentD1
            }
            Log.d(
                "SensorRotationBottomPair",
                "D higher by gravity: fixed=D1, moved=C1, shiftProgress=$fixedPointShiftProgress"
            )
            BottomPair(
                c1 = c1,
                d1 = d1
            )
        }
    }

    private fun resolveFixedPointShiftProgress(rotationDegrees: Float): Float {
        val absoluteAngle = kotlin.math.abs(rotationDegrees)
        if (absoluteAngle <= 45f) return 0f
        if (absoluteAngle >= 90f) return 1f
        return (absoluteAngle - 45f) / 45f
    }

    private fun horizontalRightAtBoundary(rotationDegrees: Float): Offset {
        val boundaryAngle = if (rotationDegrees >= 0f) 45f else -45f
        val radians = Math.toRadians(boundaryAngle.toDouble())
        return Offset(
            x = cos(radians).toFloat(),
            y = sin(radians).toFloat()
        )
    }

    private fun lerp(start: Offset, end: Offset, progress: Float): Offset {
        val clampedProgress = progress.coerceIn(0f, 1f)
        return Offset(
            x = start.x + (end.x - start.x) * clampedProgress,
            y = start.y + (end.y - start.y) * clampedProgress
        )
    }

    private fun mirrorVertically(point: Offset, centerX: Float): Offset = Offset(
        x = centerX - (point.x - centerX),
        y = point.y
    )

    private fun mirrorHorizontally(point: Offset, centerY: Float): Offset = Offset(
        x = point.x,
        y = centerY - (point.y - centerY)
    )

    private fun intersectRayWithSegment(
        rayOrigin: Offset,
        rayDirection: Offset,
        segmentStart: Offset,
        segmentEnd: Offset
    ): Offset {
        val segmentDirection = segmentEnd - segmentStart
        val denominator = cross(rayDirection, segmentDirection)
        if (denominator == 0f) return segmentStart

        val diff = segmentStart - rayOrigin
        val rayT = cross(diff, segmentDirection) / denominator
        val segmentT = cross(diff, rayDirection) / denominator

        if (rayT < 0f) return segmentStart

        val clampedSegmentT = segmentT.coerceIn(0f, 1f)
        return segmentStart + segmentDirection * clampedSegmentT
    }

    private fun cross(a: Offset, b: Offset): Float = a.x * b.y - a.y * b.x

    private fun dot(a: Offset, b: Offset): Float = a.x * b.x + a.y * b.y
}

private data class TopPair(
    val a1: Offset,
    val b1: Offset
)

private data class BottomPair(
    val c1: Offset,
    val d1: Offset
)
