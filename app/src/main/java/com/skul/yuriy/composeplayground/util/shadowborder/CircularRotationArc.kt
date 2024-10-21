package com.skul.yuriy.composeplayground.util.shadowborder

import android.graphics.BlurMaskFilter
import android.graphics.RectF
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.asin
import kotlin.math.ceil

enum class ArcPaddingType {
    HALF_INSIDE_HALF_OUTSIDE,
    FULLY_OUTSIDE,
    FULLY_INSIDE,
    ONLY_BODY_OUTSIDE,
    ONLY_BODY_INSIDE
}


fun Modifier.developSnake(
    bodyColor: Color = Color.Green,
    glowShadowColor: Color = Color.Green.copy(alpha = 0.8f),
    rotationDegrees: Float = 0f,
    bodyStrokeWidth: Dp = 12.dp,
    glowingShadowWidth: Dp = 24.dp,
    glowingBlurRadius: Dp = glowingShadowWidth / 2,
    arcPaddingType: ArcPaddingType
): Modifier = this
    .drawWithCache {

        val bodyStrokeWidthPx = bodyStrokeWidth.toPx()
        val glowingStrokeWidthPx = glowingShadowWidth.toPx()
        val glowingBlurRadiusPx = glowingBlurRadius.toPx()

        val bodyLength = 270f

        // Determine the arc padding based on the ArcPaddingType enum
        val arcPaddingPx = when (arcPaddingType) {
            ArcPaddingType.HALF_INSIDE_HALF_OUTSIDE -> 0f
            ArcPaddingType.FULLY_OUTSIDE -> glowingStrokeWidthPx / 2
            ArcPaddingType.FULLY_INSIDE -> -glowingStrokeWidthPx / 2
            ArcPaddingType.ONLY_BODY_OUTSIDE -> bodyStrokeWidthPx / 2
            ArcPaddingType.ONLY_BODY_INSIDE -> -bodyStrokeWidthPx / 2
        }


        // Define the boundaries of the arc, offsetting based on the arc padding
        // Offset the arc to either extend inside or outside
        val arcBounds = RectF(
            -arcPaddingPx,
            -arcPaddingPx,
            size.width + arcPaddingPx,
            size.height + arcPaddingPx
        )

 // we don't add offset for proper coloring round caps,
 // we add sweep gradient to the rest circular path  properly
 // A small offset for round caps to ensure correct color rendering on the round ends
 //        val roundCapsOffset = computeOffset(....)
        val roundCapsOffset = 0f

        val headPosition = bodyLength / 360f
        val opaqueToTransparentPoint = headPosition + (1f - headPosition) / 2
// Due to the limitations of BlurMaskFilter, we must use native Paint instead of a Compose Brush
// to apply the blur effect. As a result, for drawing the arc with the native Canvas, we need to use
// the native SweepGradient object rather than the Compose SweepGradient.
        val nativeSweepGradient = android.graphics.SweepGradient(
            size.width / 2 + arcPaddingPx,
            size.height / 2 + arcPaddingPx,
            intArrayOf(
                bodyColor.copy(alpha = 0f).toArgb(), // Transparent tail
                bodyColor.toArgb(),// Opaque head
                //workaround
                bodyColor.toArgb(), //makes head round cup opaque color
                bodyColor.copy(alpha = 0f).toArgb() // makes tail round cup transparent

            ),
            floatArrayOf(
                0f, // Transparent tail
                headPosition,// Opaque head
                //workaround
                opaqueToTransparentPoint,//makes head round cup opaque color
                opaqueToTransparentPoint//fix round cat tail color. makes it transparent

            ),
        )

        val gradientBrush = Brush.sweepGradient(
            colorStops = arrayOf(
                0.0f to bodyColor.copy(alpha = 0f),   // Fully opaque at the tail
                headPosition to bodyColor.copy(alpha = 1f),  // Fully transparent at head = bodyLength

                opaqueToTransparentPoint to bodyColor.copy(alpha = 1f), // makes head round cup opaque color
                opaqueToTransparentPoint to bodyColor.copy(alpha = 0f), // makes tail round cup transparent
            ),
            center = Offset(
                size.width / 2,
                size.height / 2
            ),
        )

        // Applying a blur effect only to the outer glowing arc is not possible using Compose's Brush.
        // To achieve this, we use the native Paint class with a BlurMaskFilter and draw the arc using the
        // native Android Canvas to handle the blur for the glowing effect.
        val glowingPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true // Enable anti-aliasing
                color = glowShadowColor.toArgb()
                strokeWidth = glowingStrokeWidthPx
                style = android.graphics.Paint.Style.STROKE
                strokeJoin = android.graphics.Paint.Join.ROUND
                strokeCap = android.graphics.Paint.Cap.ROUND
                //add sweep gradient
                shader = nativeSweepGradient
                //apply the blur effect
                maskFilter = BlurMaskFilter(
                    glowingBlurRadiusPx,
                    BlurMaskFilter.Blur.NORMAL
                )
            }
        }.asFrameworkPaint()


        val topLeft = Offset(-arcPaddingPx, -arcPaddingPx)
        onDrawBehind {
            rotate(rotationDegrees) {

                //draw with the help of native canvas blurred and sweep-gradient  glowing ark
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawArc(
                        arcBounds,
                        roundCapsOffset,
                        bodyLength,
                        false,
                        glowingPaint
                    )
                }

                //draw with the help of native canvas sweep-gradient body ark
                drawArc(
                    brush = gradientBrush,
                    startAngle = roundCapsOffset,
                    sweepAngle = bodyLength,
                    useCenter = false,
                    size = Size(arcBounds.width(), arcBounds.height()),
                    topLeft = topLeft,
                    style = Stroke(bodyStrokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }
    }

//compute round caps offset
private fun computeOffset(capRadius: Float, arcRadius: Float): Float {
    val sinus = (capRadius / arcRadius).toDouble()
    val degreeRad = asin(sinus)
    val degree = Math.toDegrees(degreeRad)
    return ceil(degree).toFloat()
}