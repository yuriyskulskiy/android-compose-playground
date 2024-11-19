package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

fun Modifier.colorMatrix(matrix: ColorMatrix) = this.then(ColorMatrixModifier(matrix))

fun alphaFilterColorMatrix() = ColorMatrix(
    floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 39f, -5000f // Alpha manipulation
    )
)

class ColorMatrixModifier(private val matrix: ColorMatrix) : DrawModifier {
    override fun ContentDrawScope.draw() {
        val colorFilter = ColorFilter.colorMatrix(matrix)
        val paint = Paint().apply {
            this.colorFilter = colorFilter
        }
        val bounds = Rect(0f, 0f, size.width, size.height)
        drawIntoCanvas { canvas ->
            canvas.saveLayer(bounds, paint)
            drawContent()
            canvas.restore()
        }
    }
}


//alternative way like colorMatrix
fun Modifier.colorMatrixWithContent(matrix: ColorMatrix): Modifier = this.then(
    Modifier.drawWithContent {
        val colorFilter = ColorFilter.colorMatrix(matrix)
        val paint = Paint().apply {
            this.colorFilter = colorFilter
        }
        val bounds = Rect(0f, 0f, size.width, size.height)

        drawIntoCanvas { canvas ->
            canvas.saveLayer(bounds, paint)
            drawContent() // Draw the composable content with the ColorMatrix applied
            canvas.restore()
        }
    }
)
