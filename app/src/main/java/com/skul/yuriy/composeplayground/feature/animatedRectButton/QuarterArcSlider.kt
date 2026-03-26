package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

internal enum class CornerSliderCorner {
    TopStart,
    TopEnd,
    BottomEnd,
    BottomStart
}

@Composable
internal fun QuarterArcSlider(
    corner: CornerSliderCorner,
    value: Float,
    onValueChange: (Float) -> Unit,
    maxValue: Dp,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.Gray,
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val sliderSize = 100.dp
    val touchPadding = 16.dp
    val touchTargetSize = sliderSize + touchPadding * 2
    val sliderSizePx = with(density) { sliderSize.toPx() }
    val touchPaddingPx = with(density) { touchPadding.toPx() }
    val arcPaddingPx = with(density) { 14.dp.toPx() }
    val strokeWidthPx = with(density) { 6.dp.toPx() }
    val thumbRadiusPx = with(density) { 8.dp.toPx() }
    val thumbCenter = remember(value, density, corner) {
        thumbPositionPx(
            corner = corner,
            value = value,
            sizePx = sliderSizePx,
            paddingPx = arcPaddingPx
        )
    }
    val labelOffset = remember(density, corner, thumbCenter) {
        labelOffsetPx(corner, density, thumbCenter)
    }
    val currentValueLabel = remember(value, maxValue) {
        (value.coerceIn(0f, 1f) * maxValue.value).roundToInt().toString()
    }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(touchTargetSize)
            .pointerInput(corner, maxValue, density) {
                handleQuarterArcSliderGestures(
                    corner = corner,
                    sliderSizePx = sliderSizePx,
                    touchPaddingPx = touchPaddingPx,
                    arcPaddingPx = arcPaddingPx,
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onValueChange = onValueChange
                )
            }
    ) {
        Box(
            modifier = Modifier
                .padding(touchPadding)
                .size(sliderSize)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val spec = cornerArcSpec(corner, sizePx = size.minDimension, paddingPx = arcPaddingPx)
                drawArc(
                    color = inactiveColor,
                    startAngle = spec.trackStartAngle,
                    sweepAngle = spec.trackSweepAngle,
                    useCenter = false,
                    topLeft = Offset(spec.center.x - spec.radius, spec.center.y - spec.radius),
                    size = androidx.compose.ui.geometry.Size(spec.radius * 2f, spec.radius * 2f),
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )

                drawArc(
                    color = activeColor,
                    startAngle = spec.activeStartAngle(value),
                    sweepAngle = spec.activeSweepAngle(value),
                    useCenter = false,
                    topLeft = Offset(spec.center.x - spec.radius, spec.center.y - spec.radius),
                    size = androidx.compose.ui.geometry.Size(spec.radius * 2f, spec.radius * 2f),
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )

                drawCircle(
                    color = activeColor,
                    radius = thumbRadiusPx,
                    center = thumbCenter
                )
            }

            if (isDragging) {
                Text(
                    text = currentValueLabel,
                    color = activeColor,
                    modifier = Modifier.offset {
                        IntOffset(labelOffset.x.roundToInt(), labelOffset.y.roundToInt())
                    }
                )
            }
        }
    }
}

private suspend fun PointerInputScope.handleQuarterArcSliderGestures(
    corner: CornerSliderCorner,
    sliderSizePx: Float,
    touchPaddingPx: Float,
    arcPaddingPx: Float,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onValueChange: (Float) -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        onDragStart()
        updateQuarterArcSliderValueFromTouch(
            touch = down.position,
            corner = corner,
            sliderSizePx = sliderSizePx,
            touchPaddingPx = touchPaddingPx,
            arcPaddingPx = arcPaddingPx,
            onValueChange = onValueChange
        )
        down.consume()

        do {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull() ?: break
            if (change.pressed) {
                updateQuarterArcSliderValueFromTouch(
                    touch = change.position,
                    corner = corner,
                    sliderSizePx = sliderSizePx,
                    touchPaddingPx = touchPaddingPx,
                    arcPaddingPx = arcPaddingPx,
                    onValueChange = onValueChange
                )
                change.consume()
            }
        } while (event.changes.any { it.pressed })

        onDragEnd()
    }
}

private fun updateQuarterArcSliderValueFromTouch(
    touch: Offset,
    corner: CornerSliderCorner,
    sliderSizePx: Float,
    touchPaddingPx: Float,
    arcPaddingPx: Float,
    onValueChange: (Float) -> Unit,
) {
    onValueChange(
        valueFromTouch(
            touch = touch - Offset(touchPaddingPx, touchPaddingPx),
            corner = corner,
            sizePx = sliderSizePx,
            paddingPx = arcPaddingPx
        )
    )
}

private class CornerArcSpec(
    val center: Offset,
    val radius: Float,
    val trackStartAngle: Float,
    val trackSweepAngle: Float,
    val activeStartAngle: (Float) -> Float,
    val activeSweepAngle: (Float) -> Float,
)

private fun cornerArcSpec(
    corner: CornerSliderCorner,
    sizePx: Float,
    paddingPx: Float,
): CornerArcSpec {
    val radius = sizePx - paddingPx * 2f
    return when (corner) {
        CornerSliderCorner.TopStart -> CornerArcSpec(
            center = Offset(sizePx - paddingPx, sizePx - paddingPx),
            radius = radius,
            trackStartAngle = 180f,
            trackSweepAngle = 90f,
            activeStartAngle = { 180f },
            activeSweepAngle = { it.coerceIn(0f, 1f) * 90f }
        )
        CornerSliderCorner.TopEnd -> CornerArcSpec(
            center = Offset(paddingPx, sizePx - paddingPx),
            radius = radius,
            trackStartAngle = 360f,
            trackSweepAngle = -90f,
            activeStartAngle = { 360f },
            activeSweepAngle = { -it.coerceIn(0f, 1f) * 90f }
        )
        CornerSliderCorner.BottomEnd -> CornerArcSpec(
            center = Offset(paddingPx, paddingPx),
            radius = radius,
            trackStartAngle = 90f,
            trackSweepAngle = -90f,
            activeStartAngle = { 90f },
            activeSweepAngle = { -it.coerceIn(0f, 1f) * 90f }
        )
        CornerSliderCorner.BottomStart -> CornerArcSpec(
            center = Offset(sizePx - paddingPx, paddingPx),
            radius = radius,
            trackStartAngle = 90f,
            trackSweepAngle = 90f,
            activeStartAngle = { 90f },
            activeSweepAngle = { it.coerceIn(0f, 1f) * 90f }
        )
    }
}

private fun thumbPositionPx(
    corner: CornerSliderCorner,
    value: Float,
    sizePx: Float,
    paddingPx: Float,
): Offset {
    val spec = cornerArcSpec(corner, sizePx, paddingPx)
    val angleDegrees = when (corner) {
        CornerSliderCorner.TopStart -> 180f + value.coerceIn(0f, 1f) * 90f
        CornerSliderCorner.TopEnd -> 360f - value.coerceIn(0f, 1f) * 90f
        CornerSliderCorner.BottomEnd -> 90f - value.coerceIn(0f, 1f) * 90f
        CornerSliderCorner.BottomStart -> 90f + value.coerceIn(0f, 1f) * 90f
    }
    val angleRadians = angleDegrees * PI.toFloat() / 180f
    return Offset(
        x = spec.center.x + spec.radius * kotlin.math.cos(angleRadians),
        y = spec.center.y + spec.radius * kotlin.math.sin(angleRadians)
    )
}

private fun valueFromTouch(
    touch: Offset,
    corner: CornerSliderCorner,
    sizePx: Float,
    paddingPx: Float,
): Float {
    val spec = cornerArcSpec(corner, sizePx, paddingPx)
    val rawAngle = normalizeDegrees(
        Math.toDegrees(
            atan2(
                (touch.y - spec.center.y).toDouble(),
                (touch.x - spec.center.x).toDouble()
            )
        ).toFloat()
    )
    val angle = clampAngleToCorner(rawAngle, corner)
    return when (corner) {
        CornerSliderCorner.TopStart -> ((angle - 180f) / 90f).coerceIn(0f, 1f)
        CornerSliderCorner.TopEnd -> {
            val adjusted = if (angle < 270f) angle + 360f else angle
            ((360f - adjusted) / 90f).coerceIn(0f, 1f)
        }
        CornerSliderCorner.BottomEnd -> ((90f - angle) / 90f).coerceIn(0f, 1f)
        CornerSliderCorner.BottomStart -> ((angle - 90f) / 90f).coerceIn(0f, 1f)
    }
}

private fun clampAngleToCorner(angle: Float, corner: CornerSliderCorner): Float = when (corner) {
    CornerSliderCorner.TopStart -> angle.coerceIn(180f, 270f)
    CornerSliderCorner.TopEnd -> when {
        angle in 270f..360f -> angle
        angle < 135f -> 360f
        else -> 270f
    }
    CornerSliderCorner.BottomEnd -> when {
        angle in 0f..90f -> angle
        angle < 180f -> 90f
        else -> 0f
    }
    CornerSliderCorner.BottomStart -> angle.coerceIn(90f, 180f)
}

private fun normalizeDegrees(angle: Float): Float {
    val normalized = angle % 360f
    return if (normalized < 0f) normalized + 360f else normalized
}

private fun labelOffsetPx(
    corner: CornerSliderCorner,
    density: Density,
    thumbCenter: Offset,
): Offset {
    val dx = with(density) { 12.dp.toPx() }
    val dy = with(density) { 10.dp.toPx() }
    val labelWidth = with(density) { 36.dp.toPx() }
    val labelHeight = with(density) { 28.dp.toPx() }
    return when (corner) {
        CornerSliderCorner.TopStart -> Offset(thumbCenter.x - labelWidth, thumbCenter.y - labelHeight)
        CornerSliderCorner.TopEnd -> Offset(thumbCenter.x + dx, thumbCenter.y - labelHeight)
        CornerSliderCorner.BottomEnd -> Offset(thumbCenter.x + dx, thumbCenter.y + dy)
        CornerSliderCorner.BottomStart -> Offset(thumbCenter.x - labelWidth, thumbCenter.y + dy)
    }
}

private fun Dp.toPx(density: Density): Float = with(density) { this@toPx.toPx() }
