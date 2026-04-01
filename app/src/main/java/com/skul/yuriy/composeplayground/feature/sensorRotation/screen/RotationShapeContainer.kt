package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator

@Composable
internal fun RotationShapeContainer(
    modifier: Modifier = Modifier,
    inset: Dp,
    rotationDegrees: Float,
    shapeCalculator: IRotationShapeCalculator,
    rotateContentWithShape: Boolean,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val shape =
        RotationShapeOutline(
            inset = inset,
            rotationDegrees = rotationDegrees,
            shapeCalculator = shapeCalculator,
        )

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White)
    ) {
        Box(
            modifier =
                if (rotateContentWithShape) {
                    Modifier.graphicsLayer {
                        rotationZ = rotationDegrees
                        transformOrigin = TransformOrigin.Center
                    }
                } else {
                    Modifier
                }
        ) {
            content()
        }
    }
}

private class RotationShapeOutline(
    private val inset: Dp,
    private val rotationDegrees: Float,
    private val shapeCalculator: IRotationShapeCalculator,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val insetPx = with(density) { inset.toPx() }
        val shapePoints = shapeCalculator.calculate(
            anchorA = Offset(insetPx, insetPx),
            anchorB = Offset(size.width - insetPx, insetPx),
            anchorC = Offset(size.width - insetPx, size.height - insetPx),
            anchorD = Offset(insetPx, size.height - insetPx),
            rotationDegrees = rotationDegrees,
        )

        return Outline.Generic(
            Path().apply {
                moveTo(shapePoints.a1.x, shapePoints.a1.y)
                lineTo(shapePoints.b1.x, shapePoints.b1.y)
                lineTo(shapePoints.c1.x, shapePoints.c1.y)
                lineTo(shapePoints.d1.x, shapePoints.d1.y)
                close()
            }
        )
    }
}
