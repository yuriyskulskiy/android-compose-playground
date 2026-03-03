package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.shadowlayer

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp



// could be applied to any shape
fun Modifier.drawOutlineShadowLayerShadow(
    color: Color,
    haloBorderWidth: Dp,
    cornerRadius: Dp,
    passesCount: Int,
    shadowOffsetX: Dp = 0.dp,
    shadowOffsetY: Dp = 0.dp
): Modifier = if (haloBorderWidth > 0.dp) {
    val shape = RoundedCornerShape(cornerRadius)
    this.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }

        val shadowRadiusPx = haloBorderWidth.toPx()
        val shadowOffsetXPx = shadowOffsetX.toPx()
        val shadowOffsetYPx = shadowOffsetY.toPx()

        val shadowPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = color.copy(alpha = 0.01f).toArgb()
                setShadowLayer(
                    shadowRadiusPx,
                    shadowOffsetXPx,
                    shadowOffsetYPx,
                    color.toArgb()
                )
            }
        }
        val passes = passesCount.coerceAtLeast(0)

        onDrawWithContent {
            clipPath(path, ClipOp.Difference) {
                drawIntoCanvas { canvas ->
                    repeat(passes) {
                        canvas.drawPath(path, shadowPaint)
                    }
                }
            }
            drawContent()
        }
    }
} else {
    this
}
