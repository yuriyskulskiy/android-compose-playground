package com.skul.yuriy.composeplayground.util.shadowborder

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.alpha
import kotlin.math.max
import android.graphics.Path as AndroidPath

//TODO WIP
fun Modifier.pathSnakeBorder(
    bodyColor: Color = Color.Green,
    glowShadowColor: Color = Color.Green.copy(alpha = 0.8f),
    progress: Float = 0f,
    cornerRadius: Dp = 28.dp,
    snakeLengthFraction: Float = 0.28f,
    bodyStrokeWidth: Dp = 2.dp,
    glowingShadowWidth: Dp = 12.dp,
    glowingBlurRadius: Dp = glowingShadowWidth / 2
): Modifier = this
    .drawWithCache {
        val bodyStrokeWidthPx = bodyStrokeWidth.toPx()
        val glowingStrokeWidthPx = glowingShadowWidth.toPx()
        val glowingBlurRadiusPx = glowingBlurRadius.toPx()
        val cornerRadiusPx = cornerRadius.toPx()
        val borderInset = max(bodyStrokeWidthPx, glowingStrokeWidthPx) / 2f + 1f

        val trackRect = RectF(
            borderInset,
            borderInset,
            size.width - borderInset,
            size.height - borderInset
        )

        val trackWidth = trackRect.width()
        val trackHeight = trackRect.height()
        if (trackWidth <= 0f || trackHeight <= 0f) {
            return@drawWithCache onDrawBehind {}
        }

        val maxAllowedCorner = minOf(trackWidth, trackHeight) / 2f
        val finalCornerRadius = minOf(cornerRadiusPx, maxAllowedCorner)

        val fullTrackPath = AndroidPath().apply {
            addRoundRect(
                trackRect,
                finalCornerRadius,
                finalCornerRadius,
                AndroidPath.Direction.CW
            )
        }
        val pathMeasure = PathMeasure(fullTrackPath, true)
        val totalLength = pathMeasure.length
        if (totalLength <= 0f) {
            return@drawWithCache onDrawBehind {}
        }

        val clampedSnakeLengthFraction = snakeLengthFraction.coerceIn(0.05f, 0.95f)
        val snakeLength = totalLength * clampedSnakeLengthFraction

        val startDistance = progress * totalLength
        val endDistance = startDistance + snakeLength
        val wrappedEndDistance = endDistance % totalLength

        val snakePath = AndroidPath()
        snakePath.close()
        if (endDistance <= totalLength) {
            pathMeasure.getSegment(startDistance, endDistance, snakePath, true)
        } else {
            pathMeasure.getSegment(startDistance, totalLength, snakePath, true)
            pathMeasure.getSegment(0f, wrappedEndDistance, snakePath, true)
        }

        val tailPos = FloatArray(2)
        val headPos = FloatArray(2)
        pathMeasure.getPosTan(startDistance, tailPos, null)
        pathMeasure.getPosTan(wrappedEndDistance, headPos, null)

        val gradientEndX = if (tailPos[0] == headPos[0] && tailPos[1] == headPos[1]) {
            headPos[0] + 0.001f
        } else {
            headPos[0]
        }
        val gradientEndY = headPos[1]

        val bodyShader = LinearGradient(
            tailPos[0],
            tailPos[1],
            gradientEndX,
            gradientEndY,
            intArrayOf(
                bodyColor.copy(alpha = 0f).toArgb(),
                bodyColor.toArgb()
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        val glowShader = LinearGradient(
            tailPos[0],
            tailPos[1],
            gradientEndX,
            gradientEndY,
            intArrayOf(
                glowShadowColor.copy(alpha = 0f).toArgb(),
                glowShadowColor.toArgb()
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        val bodyPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = bodyStrokeWidthPx
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
            shader = bodyShader
        }

        val glowPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = glowingStrokeWidthPx
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
            shader = glowShader
            maskFilter = BlurMaskFilter(glowingBlurRadiusPx, BlurMaskFilter.Blur.NORMAL)
        }

        onDrawBehind {
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawPath(snakePath, glowPaint)
                canvas.nativeCanvas.drawPath(snakePath, bodyPaint)
            }
        }
    }



