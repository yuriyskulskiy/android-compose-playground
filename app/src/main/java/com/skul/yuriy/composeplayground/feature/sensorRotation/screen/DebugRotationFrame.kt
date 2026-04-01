package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator

@Composable
internal fun DebugRotationFrame(
    modifier: Modifier = Modifier,
    inset: Dp,
    rotationDegrees: Float,
    shapeCalculator: IRotationShapeCalculator,
    calculatorLabel: String,
    onSwitchCalculator: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .border(width = 1.dp, color = Color.Red)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val insetPx = inset.toPx()
            val markerRadius = 3.dp.toPx()
            val anchorMarkerRadius = 3.dp.toPx()
            val insetMarkerRadius = 5.dp.toPx()

            val topLeft = Offset(0f, 0f)
            val topRight = Offset(size.width, 0f)
            val bottomRight = Offset(size.width, size.height)
            val bottomLeft = Offset(0f, size.height)

            val insetTopLeft = Offset(insetPx, insetPx)
            val insetTopRight = Offset(size.width - insetPx, insetPx)
            val insetBottomRight = Offset(size.width - insetPx, size.height - insetPx)
            val insetBottomLeft = Offset(insetPx, size.height - insetPx)
            val shapePoints = shapeCalculator.calculate(
                anchorA = insetTopLeft,
                anchorB = insetTopRight,
                anchorC = insetBottomRight,
                anchorD = insetBottomLeft,
                rotationDegrees = rotationDegrees
            )

            drawCircle(color = Color.Blue, radius = markerRadius, center = topLeft)
            drawCircle(color = Color.Blue, radius = markerRadius, center = topRight)
            drawCircle(color = Color.Blue, radius = markerRadius, center = bottomRight)
            drawCircle(color = Color.Blue, radius = markerRadius, center = bottomLeft)

            drawPath(
                path = Path().apply {
                    moveTo(shapePoints.a1.x, shapePoints.a1.y)
                    lineTo(shapePoints.b1.x, shapePoints.b1.y)
                    lineTo(shapePoints.c1.x, shapePoints.c1.y)
                    lineTo(shapePoints.d1.x, shapePoints.d1.y)
                    close()
                },
                color = Color.Green,
                style = Stroke(width = 2.dp.toPx())
            )

            drawCircle(color = Color.Green, radius = insetMarkerRadius, center = shapePoints.a1)
            drawCircle(color = Color.Green, radius = insetMarkerRadius, center = shapePoints.b1)
            drawCircle(color = Color.Green, radius = insetMarkerRadius, center = shapePoints.c1)
            drawCircle(color = Color.Green, radius = insetMarkerRadius, center = shapePoints.d1)

            drawCircle(color = Color.Black, radius = anchorMarkerRadius, center = insetTopLeft)
            drawCircle(color = Color.Black, radius = anchorMarkerRadius, center = insetTopRight)
            drawCircle(color = Color.Black, radius = anchorMarkerRadius, center = insetBottomRight)
            drawCircle(color = Color.Black, radius = anchorMarkerRadius, center = insetBottomLeft)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = rotationDegrees
                    transformOrigin = TransformOrigin.Center
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerY = size.height / 2f
                drawLine(
                    color = Color.Black,
                    start = Offset(x = 0f, y = centerY),
                    end = Offset(x = size.width, y = centerY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-16).dp),
                text = "Tilt: ${"%.1f".format(rotationDegrees)}°",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = onSwitchCalculator) {
                    Text(calculatorLabel)
                }
            }
        }
    }
}
