package com.skul.yuriy.composeplayground.util.shadowborder.rect

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

//any shape
fun Modifier.drawOutlineMultiLayerShadow(
    color: Color,
    haloBorderWidth: Dp,
    cornerRadius: Dp
): Modifier = if (haloBorderWidth > 0.dp) {
    this.drawBehind {
        val layers = 30
        val haloBorderWidthPx = haloBorderWidth.toPx()
        val cornerRadiusPx = cornerRadius.toPx()

        repeat(layers) { index ->
            val progress = index / (layers - 1).toFloat()
            val strokeWidth = 1f + progress * haloBorderWidthPx
            val expansion = strokeWidth / 2f
            val alpha = (1f - progress) * 0.2f

            drawRoundRect(
//                color = color.copy(alpha = alpha),
                color = Color(0xFF4DD5FF).copy(alpha = alpha),
                topLeft = Offset(-expansion, -expansion),
                size = Size(
                    width = size.width + expansion * 2f,
                    height = size.height + expansion * 2f
                ),
                cornerRadius = CornerRadius(
                    x = cornerRadiusPx + expansion,
                    y = cornerRadiusPx + expansion
                ),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }
} else {
    this
}