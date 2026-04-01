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
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.ShapePoints
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
internal fun RotationShapeContainer(
    modifier: Modifier = Modifier,
    inset: Dp,
    rotationDegrees: Float,
    shapeCalculator: IRotationShapeCalculator,
    rotateContentWithShape: Boolean,
    content: @Composable BoxScope.() -> Unit = {}
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
                "RotationShapeContainer",
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
                .background(Color.White)
        ) {
            val resolvedContentSize =
                if (rotateContentWithShape) {
                    resolvePreRotationContentSize(
                        shapePoints = layoutData.shapePoints,
                        center = layoutData.center,
                        rotationDegrees = rotationDegrees,
                    )
                } else {
                    layoutData.contentSize
                }
            val contentWidth =
                with(density) {
                    if (rotateContentWithShape) {
                        resolvedContentSize.width.toDp()
                    } else {
                        resolvedContentSize.width.toDp()
                    }
                }
            val contentHeight = with(density) { resolvedContentSize.height.toDp() }

            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .requiredSize(width = contentWidth, height = contentHeight)
                        .then(
                            if (rotateContentWithShape) {
                                Modifier.graphicsLayer {
                                    rotationZ = rotationDegrees
                                    transformOrigin = TransformOrigin.Center
                                }
                            } else {
                                Modifier
                            }
                        )
            ) {
                content()
            }
        }
    }
}

private fun resolvePreRotationContentSize(
    shapePoints: ShapePoints,
    center: Offset,
    rotationDegrees: Float,
): Size {
    val radians = Math.toRadians((-rotationDegrees).toDouble())
    val cosValue = cos(radians).toFloat()
    val sinValue = sin(radians).toFloat()

    fun unrotate(point: Offset): Offset {
        val translatedX = point.x - center.x
        val translatedY = point.y - center.y
        return Offset(
            x = translatedX * cosValue - translatedY * sinValue,
            y = translatedX * sinValue + translatedY * cosValue,
        )
    }

    val localA = unrotate(shapePoints.a1)
    val localB = unrotate(shapePoints.b1)
    val localC = unrotate(shapePoints.c1)
    val localD = unrotate(shapePoints.d1)
    val minX = min(min(localA.x, localB.x), min(localC.x, localD.x))
    val maxX = max(max(localA.x, localB.x), max(localC.x, localD.x))
    val minY = min(min(localA.y, localB.y), min(localC.y, localD.y))
    val maxY = max(max(localA.y, localB.y), max(localC.y, localD.y))

    return Size(
        width = maxX - minX,
        height = maxY - minY,
    )
}

private fun distance(start: Offset, end: Offset): Float {
    val dx = end.x - start.x
    val dy = end.y - start.y
    return kotlin.math.sqrt(dx * dx + dy * dy)
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
