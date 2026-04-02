package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.ShapePoints
import kotlin.math.abs
import kotlin.math.atan2

@Composable
internal fun RotationHost(
    modifier: Modifier = Modifier,
    inset: Dp,
    rotationDegrees: Float,
    shapeCalculator: IRotationShapeCalculator,
    rotateContentWithShape: Boolean,
    content: @Composable BoxScope.(RotationShapeTextLayoutInfo) -> Unit = {}
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val density = LocalDensity.current
        val insetPx = with(density) { inset.toPx() }
        val layoutData =
            shapeCalculator.calculate(
                anchorA = Offset(insetPx, insetPx),
                anchorB = Offset(constraints.maxWidth.toFloat() - insetPx, insetPx),
                anchorC = Offset(
                    constraints.maxWidth.toFloat() - insetPx,
                    constraints.maxHeight.toFloat() - insetPx,
                ),
                anchorD = Offset(insetPx, constraints.maxHeight.toFloat() - insetPx),
                rotationDegrees = rotationDegrees,
            )
        val shape =
            RotationShapeOutline(
                path = layoutData.path,
            )
        LaunchedEffect(
            layoutData.contentSize.width,
            layoutData.contentSize.height,
            layoutData.shapePoints.a1,
            layoutData.shapePoints.b1,
            layoutData.shapePoints.c1,
            rotationDegrees,
            rotateContentWithShape,
        ) {
            val widthByPoints = distance(layoutData.shapePoints.a1, layoutData.shapePoints.b1)
            val heightByPoints = distance(layoutData.shapePoints.b1, layoutData.shapePoints.c1)
            Log.wtf(
                "RotationHost",
                "contentSizePx width=${layoutData.contentSize.width}, " +
                    "height=${layoutData.contentSize.height}, " +
                    "a1b1=$widthByPoints, " +
                    "b1c1=$heightByPoints, " +
                    "rotation=$rotationDegrees, " +
                    "rotateContentWithShape=$rotateContentWithShape",
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(Color.Black)
        ) {
            val parallelogramFrame =
                if (rotateContentWithShape) {
                    resolveParallelogramFrame(
                        shapePoints = layoutData.shapePoints,
                    )
                } else {
                    ParallelogramFrame(
                        contentBoundingWidth = layoutData.contentSize.width,
                        contentHeight = layoutData.contentSize.height,
                        lineWidth = layoutData.contentSize.width,
                        firstLineOffset = 0f,
                        horizontalShiftPerHeight = 0f,
                    )
                }
            val contentWidth =
                with(density) {
                    if (rotateContentWithShape) {
                        parallelogramFrame.contentBoundingWidth.toDp()
                    } else {
                        parallelogramFrame.contentBoundingWidth.toDp()
                    }
                }
            val contentHeight = with(density) { parallelogramFrame.contentHeight.toDp() }
            val contentLayoutInfo =
                RotationShapeTextLayoutInfo(
                    contentWidth = contentWidth,
                    contentHeight = contentHeight,
                    lineWidth = with(density) { parallelogramFrame.lineWidth.toDp() },
                    firstLineOffset = with(density) { parallelogramFrame.firstLineOffset.toDp() },
                    topEdgeRotationDegrees = topEdgeRotationDegrees(
                        start = layoutData.shapePoints.a1,
                        end = layoutData.shapePoints.b1,
                    ),
                    horizontalShiftPerHeight = parallelogramFrame.horizontalShiftPerHeight,
                )

            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .requiredSize(width = contentWidth, height = contentHeight)
                        .then(
                            if (rotateContentWithShape) {
                                Modifier.graphicsLayer {
                                    rotationZ = contentLayoutInfo.topEdgeRotationDegrees
                                    transformOrigin = TransformOrigin.Center
                                }
                            } else {
                                Modifier
                            }
                        )
            ) {
                content(contentLayoutInfo)
            }
        }
    }
}

internal data class RotationShapeTextLayoutInfo(
    val contentWidth: Dp = 0.dp,
    val contentHeight: Dp = 0.dp,
    val lineWidth: Dp = 0.dp,
    val firstLineOffset: Dp = 0.dp,
    val topEdgeRotationDegrees: Float = 0f,
    val horizontalShiftPerHeight: Float = 0f,
)

private data class ParallelogramFrame(
    val contentBoundingWidth: Float,
    val contentHeight: Float,
    val lineWidth: Float,
    val firstLineOffset: Float,
    val horizontalShiftPerHeight: Float,
)

private fun resolveParallelogramFrame(
    shapePoints: ShapePoints,
): ParallelogramFrame {
    val lineWidth = distance(shapePoints.a1, shapePoints.b1)
    val contentHeight =
        perpendicularDistanceToLine(
            point = shapePoints.d1,
            lineStart = shapePoints.a1,
            lineEnd = shapePoints.b1,
        )
    val totalHorizontalShift = horizontalShift(shapePoints)
    val horizontalShiftPerHeight =
        if (contentHeight == 0f) 0f else totalHorizontalShift / contentHeight
    val firstLineOffset = if (totalHorizontalShift < 0f) -totalHorizontalShift else 0f
    val contentBoundingWidth = lineWidth + abs(totalHorizontalShift)

    return ParallelogramFrame(
        contentBoundingWidth = contentBoundingWidth,
        contentHeight = contentHeight,
        lineWidth = lineWidth,
        firstLineOffset = firstLineOffset,
        horizontalShiftPerHeight = horizontalShiftPerHeight,
    )
}

private fun distance(start: Offset, end: Offset): Float {
    val dx = end.x - start.x
    val dy = end.y - start.y
    return kotlin.math.sqrt(dx * dx + dy * dy)
}

private fun perpendicularDistanceToLine(
    point: Offset,
    lineStart: Offset,
    lineEnd: Offset,
): Float {
    val lineLength = distance(lineStart, lineEnd)
    if (lineLength == 0f) return 0f

    val doubledTriangleArea =
        kotlin.math.abs(
            (lineEnd.x - lineStart.x) * (lineStart.y - point.y) -
                (lineStart.x - point.x) * (lineEnd.y - lineStart.y)
        )

    return doubledTriangleArea / lineLength
}

private fun topEdgeRotationDegrees(
    start: Offset,
    end: Offset,
): Float {
    return Math.toDegrees(
        atan2(
            y = (end.y - start.y).toDouble(),
            x = (end.x - start.x).toDouble(),
        )
    ).toFloat()
}

private fun horizontalShift(shapePoints: ShapePoints): Float {
    val baseVectorX = shapePoints.b1.x - shapePoints.a1.x
    val baseVectorY = shapePoints.b1.y - shapePoints.a1.y
    val sideVectorX = shapePoints.d1.x - shapePoints.a1.x
    val sideVectorY = shapePoints.d1.y - shapePoints.a1.y
    val baseLength = distance(shapePoints.a1, shapePoints.b1)
    if (baseLength == 0f) return 0f

    val unitBaseX = baseVectorX / baseLength
    val unitBaseY = baseVectorY / baseLength
    return sideVectorX * unitBaseX + sideVectorY * unitBaseY
}

private class RotationShapeOutline(
    private val path: androidx.compose.ui.graphics.Path,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        return Outline.Generic(path)
    }
}
