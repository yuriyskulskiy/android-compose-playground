package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp

//Modifier that creates a blurred around halo
fun Modifier.outlineBlur(
    blurRadius: Dp,
    shape: Shape,
    color: Color = Color.Black
): Modifier = this.then(
    Modifier.drawWithCache {
        // Native Paint with BlurMaskFilter for the halo
        val nativePaint = android.graphics.Paint().apply {
            isAntiAlias = true
            this.color = color.toArgb()
            maskFilter = android.graphics.BlurMaskFilter(
                blurRadius.toPx(),
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )

        }
        val outline = shape.createOutline(size, layoutDirection, this)
        val shapePath = Path().apply {
            addOutline(outline)
        }

        onDrawWithContent {
            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.clipPath(shapePath, ClipOp.Difference)
                canvas.nativeCanvas.drawPath(shapePath.asAndroidPath(), nativePaint)
                canvas.restore()

                // Calculate the center and radius for the circular halo
//                val center = Offset(size.width / 2, size.height / 2)
//                val radius = size.minDimension / 2 + blurRadius.toPx()
//                canvas.nativeCanvas.drawCircle(center.x, center.y, radius, nativePaint)
//                canvas.restore()
            }

            // Draw the main content (unblurred)
            drawContent()
        }
    }
)


