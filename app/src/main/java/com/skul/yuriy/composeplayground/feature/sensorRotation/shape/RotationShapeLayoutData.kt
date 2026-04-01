package com.skul.yuriy.composeplayground.feature.sensorRotation.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path

data class RotationShapeLayoutData(
    val shapePoints: ShapePoints,
    val path: Path,
    val bounds: Rect,
    val center: Offset,
    val contentSize: Size,
) {
    companion object {
        fun fromShapePoints(
            shapePoints: ShapePoints,
            contentSize: Size? = null,
        ): RotationShapeLayoutData {
            val minX = minOf(shapePoints.a1.x, shapePoints.b1.x, shapePoints.c1.x, shapePoints.d1.x)
            val maxX = maxOf(shapePoints.a1.x, shapePoints.b1.x, shapePoints.c1.x, shapePoints.d1.x)
            val minY = minOf(shapePoints.a1.y, shapePoints.b1.y, shapePoints.c1.y, shapePoints.d1.y)
            val maxY = maxOf(shapePoints.a1.y, shapePoints.b1.y, shapePoints.c1.y, shapePoints.d1.y)
            val bounds = Rect(left = minX, top = minY, right = maxX, bottom = maxY)

            return RotationShapeLayoutData(
                shapePoints = shapePoints,
                path =
                    Path().apply {
                        moveTo(shapePoints.a1.x, shapePoints.a1.y)
                        lineTo(shapePoints.b1.x, shapePoints.b1.y)
                        lineTo(shapePoints.c1.x, shapePoints.c1.y)
                        lineTo(shapePoints.d1.x, shapePoints.d1.y)
                        close()
                    },
                bounds = bounds,
                center = bounds.center,
                contentSize = contentSize ?: bounds.size,
            )
        }
    }
}

data class ShapePoints(
    val a1: Offset,
    val b1: Offset,
    val c1: Offset,
    val d1: Offset,
)
