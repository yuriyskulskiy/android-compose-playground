package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurrender

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.drawOutlineOutsideStroke(
    color: Color,
    outsideWidth: Dp,
    cornerRadius: Dp
): Modifier = if (outsideWidth > 0.dp) {
    val shape = RoundedCornerShape(cornerRadius)
    this.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }
        val strokeWidthPx = outsideWidth.toPx() * 2f

        onDrawWithContent {
            drawContent()
            clipPath(path, ClipOp.Difference) {
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = strokeWidthPx)
                )
            }
        }
    }
} else {
    this
}

fun Modifier.drawOutlineRoundedRectFill(
    color: Color,
    cornerRadius: Dp
): Modifier = this.drawWithCache {
    val shape = RoundedCornerShape(cornerRadius)
    val outline = shape.createOutline(size, layoutDirection, this)
    val path = Path().apply { addOutline(outline) }

    onDrawWithContent {
        drawContent()
        drawPath(
            path = path,
            color = color
        )
    }
}
