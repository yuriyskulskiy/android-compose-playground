package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator

@Composable
internal fun RotationShapeContainer(
    modifier: Modifier = Modifier,
    inset: Dp,
    rotationDegrees: Float,
    shapeCalculator: IRotationShapeCalculator,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(Color.White)
            .drawWithCache {
                val insetPx = inset.toPx()
                val shapePoints = shapeCalculator.calculate(
                    anchorA = Offset(insetPx, insetPx),
                    anchorB = Offset(size.width - insetPx, insetPx),
                    anchorC = Offset(size.width - insetPx, size.height - insetPx),
                    anchorD = Offset(insetPx, size.height - insetPx),
                    rotationDegrees = rotationDegrees
                )
                val path = Path().apply {
                    moveTo(shapePoints.a1.x, shapePoints.a1.y)
                    lineTo(shapePoints.b1.x, shapePoints.b1.y)
                    lineTo(shapePoints.c1.x, shapePoints.c1.y)
                    lineTo(shapePoints.d1.x, shapePoints.d1.y)
                    close()
                }

                onDrawWithContent {
                    drawContent()
                    drawPath(
                        path = path,
                        color = Color.Blue,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
    ) {
        content()
    }
}
