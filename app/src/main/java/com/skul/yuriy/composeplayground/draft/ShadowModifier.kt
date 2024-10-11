package com.skul.yuriy.composeplayground.draft

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp

//draft in progress
fun Modifier.drawOutlineCircularShadow(
    color: Color,
    blur: Dp,
    haloBorderWidth: Dp,
) =

    this
        .drawBehind {
            val haloBorderWidthPx = haloBorderWidth.toPx()
            if (haloBorderWidthPx > 0) {
                val shadowSize = Size(size.width + haloBorderWidth.toPx(), size.height + haloBorderWidth.toPx())
                val haloShadowOutline = CircleShape.createOutline(shadowSize, layoutDirection, this)
                val paint = Paint()

                paint.color = color
                paint.style = PaintingStyle.Stroke
                paint.strokeWidth = haloBorderWidthPx
                if (blur.toPx() > 0) {
                    paint
                        .asFrameworkPaint()
                        .apply {
                            maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
                        }
                }

                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.translate(
                        -haloBorderWidthPx / 2,
                        (-haloBorderWidthPx / 2)
                    )
                    canvas.drawOutline(haloShadowOutline, paint)
                    canvas.restore()


                    drawCircle(
                        radius = size.minDimension / 2,
                        center = center,
                        color = Color.Transparent,
                        blendMode = BlendMode.Clear
                    )
                }
            }
        }