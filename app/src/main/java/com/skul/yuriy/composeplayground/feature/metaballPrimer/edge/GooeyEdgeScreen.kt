package com.skul.yuriy.composeplayground.feature.metaballPrimer.edge

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun GooeyEdgeScreen(
    modifier: Modifier = Modifier,
) {
    val radiusDp: Dp = 72.dp
    val radiusPx = with(LocalDensity.current) { radiusDp.toPx() }
    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }
    var center by remember { mutableStateOf(Offset.Zero) }

    //places circle in center
    LaunchedEffect(containerWidth, containerHeight) {
        if (containerWidth > 0f && containerHeight > 0f && center == Offset.Zero) {
            center = Offset(containerWidth / 2f, containerHeight / 2f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                containerWidth = size.width.toFloat()
                containerHeight = size.height.toFloat()
            }
            .pointerInput(radiusPx, containerWidth, containerHeight) {
                detectDragGestures { change: PointerInputChange, dragAmount: Offset ->
                    change.consume()
                    val newCenter = center + dragAmount
                    val clampedX = min(max(radiusPx, newCenter.x), max(radiusPx, containerWidth - radiusPx))
                    val clampedY = min(max(radiusPx, newCenter.y), max(radiusPx, containerHeight - radiusPx))
                    center = Offset(clampedX, clampedY)
//                    center = newCenter
                }
            },
        contentAlignment = Alignment.TopStart,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(Color.Red)
        )
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (center.x - radiusPx).roundToInt(),
                        (center.y - radiusPx).roundToInt()
                    )
                }
                .background(Color.Black, shape = CircleShape)
                .size(radiusDp * 2)
        )
    }
}
