package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurmask

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.drawOutlineBlurMaskShadow(
    color: Color,
    haloBorderWidth: Dp,
    cornerRadius: Dp,
    shape: Shape? = null,
    blurRadius: Dp = haloBorderWidth
): Modifier = if (haloBorderWidth > 0.dp) {
    val resolvedShape = shape ?: RoundedCornerShape(cornerRadius)
    this.drawWithCache {
        val outline = resolvedShape.createOutline(size, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }

        val haloBorderWidthPx = haloBorderWidth.toPx()
        val blurRadiusPx = blurRadius.toPx().coerceAtLeast(0.1f)

        val shadowPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = color.toArgb()
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = haloBorderWidthPx
                maskFilter = BlurMaskFilter(blurRadiusPx, BlurMaskFilter.Blur.NORMAL)
            }
        }

        onDrawWithContent {
            drawContent()
            clipPath(path, ClipOp.Difference) {
                drawIntoCanvas { canvas ->
                    canvas.drawPath(path, shadowPaint)
                }
            }
        }
    }
} else {
    this
}
