package com.skul.yuriy.composeplayground.util.shadowborder

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

fun Modifier.rectSnakeBorder(
    bodyColor: Color = Color.Green,
    glowShadowColor: Color = Color.Green.copy(alpha = 0.8f),
    progress: Float = 0f,
    cornerRadius: Dp = 28.dp,
    snakeLengthFraction: Float = 0.28f,
    bodyStrokeWidth: Dp = 2.dp,
    glowingShadowWidth: Dp = 12.dp,
    glowingBlurRadius: Dp = glowingShadowWidth / 2
): Modifier = this.drawWithCache {
    val bodyStrokeWidthPx = bodyStrokeWidth.toPx()
    val glowingStrokeWidthPx = glowingShadowWidth.toPx()
    val cornerRadiusPx = cornerRadius.toPx()

    val borderInset = max(bodyStrokeWidthPx, glowingStrokeWidthPx) / 2f + 1f
    val left = borderInset
    val top = borderInset
    val right = size.width - borderInset
    val bottom = size.height - borderInset

    val trackWidth = right - left
    val trackHeight = bottom - top
    if (trackWidth <= 0f || trackHeight <= 0f) {
        return@drawWithCache onDrawBehind {}
    }

    val r = min(cornerRadiusPx, min(trackWidth, trackHeight) / 2f)
    val topLen = (trackWidth - 2f * r).coerceAtLeast(0f)
    val rightLen = (trackHeight - 2f * r).coerceAtLeast(0f)
    val arcQuarterLen = (PI.toFloat() * r / 2f).coerceAtLeast(0f)

    val totalLen = 2f * (topLen + rightLen) + 4f * arcQuarterLen
    if (totalLen <= 0f) {
        return@drawWithCache onDrawBehind {}
    }

    val normalizedProgress = ((progress % 1f) + 1f) % 1f
    val distance = normalizedProgress * totalLen

    val trCenter = Offset(right - r, top + r)
    val brCenter = Offset(right - r, bottom - r)
    val blCenter = Offset(left + r, bottom - r)
    val tlCenter = Offset(left + r, top + r)

    fun anglePoint(center: Offset, angleRadians: Float): Offset {
        return Offset(
            x = center.x + r * cos(angleRadians),
            y = center.y + r * sin(angleRadians)
        )
    }

    val head = run {
        var d = distance

        if (d <= topLen) {
            return@run Offset(left + r + d, top)
        }
        d -= topLen

        if (d <= arcQuarterLen && arcQuarterLen > 0f) {
            val t = d / arcQuarterLen
            val angle = (-PI / 2f + (PI / 2f) * t).toFloat()
            return@run anglePoint(trCenter, angle)
        }
        d -= arcQuarterLen

        if (d <= rightLen) {
            return@run Offset(right, top + r + d)
        }
        d -= rightLen

        if (d <= arcQuarterLen && arcQuarterLen > 0f) {
            val t = d / arcQuarterLen
            val angle = ((PI * t) / 2f).toFloat()
            return@run anglePoint(brCenter, angle)
        }
        d -= arcQuarterLen

        if (d <= topLen) {
            return@run Offset(right - r - d, bottom)
        }
        d -= topLen

        if (d <= arcQuarterLen && arcQuarterLen > 0f) {
            val t = d / arcQuarterLen
            val angle = ((PI / 2f) + (PI / 2f) * t).toFloat()
            return@run anglePoint(blCenter, angle)
        }
        d -= arcQuarterLen

        if (d <= rightLen) {
            return@run Offset(left, bottom - r - d)
        }
        d -= rightLen

        if (arcQuarterLen > 0f) {
            val t = (d / arcQuarterLen).coerceIn(0f, 1f)
            val angle = (PI + (PI / 2f) * t).toFloat()
            return@run anglePoint(tlCenter, angle)
        }

        Offset(left + r, top)
    }

    val markerRadius = max(bodyStrokeWidthPx * 1.8f, 4.dp.toPx())

    onDrawBehind {
        drawCircle(
            color = Color.Red,
            radius = markerRadius,
            center = head
        )
    }
}
