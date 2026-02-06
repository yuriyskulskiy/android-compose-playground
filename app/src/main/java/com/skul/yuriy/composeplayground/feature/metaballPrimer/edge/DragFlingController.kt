package com.skul.yuriy.composeplayground.feature.metaballPrimer.edge

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Stable
class DragFlingController(
    private val radiusPx: () -> Float,
    private val containerSize: () -> Size,
    private val overflowMultiplier: Float,
    private val velocityScale: Float,
    private val scope: CoroutineScope,
    private val decay: DecayAnimationSpec<Offset>,
) {
    private val animatable = Animatable(Offset.Zero, Offset.VectorConverter)

    var center: Offset by mutableStateOf(Offset.Zero)
        private set

    suspend fun handleDrag(pointerScope: PointerInputScope) {
        val velocityTracker = VelocityTracker()
        pointerScope.detectDragGestures(
            onDragStart = {
                velocityTracker.resetTracking()
                scope.launch { animatable.stop() }
            },
            onDragEnd = {
                val velocity = velocityTracker.calculateVelocity()
                val initialVelocity = Offset(
                    velocity.x * velocityScale,
                    velocity.y * velocityScale
                )
                scope.launch {
                    animatable.snapTo(center)
                    animatable.animateDecay(initialVelocity, decay) {
                        center = clamp(value)
                    }
                    center = clamp(animatable.value)
                    animatable.snapTo(center)
                }
            },
            onDragCancel = {
                scope.launch { animatable.stop() }
            }
        ) { change: PointerInputChange, dragAmount: Offset ->
            change.consume()
            velocityTracker.addPosition(change.uptimeMillis, change.position)
            center = clamp(center + dragAmount)
        }
    }

    fun centerIfUnset() {
        val size = containerSize()
        if (size.width > 0f && size.height > 0f && center == Offset.Zero) {
            center = Offset(size.width / 2f, size.height / 2f)
        }
    }

    private fun clamp(value: Offset): Offset {
        val overflow = radiusPx() * overflowMultiplier
        val size = containerSize()
        val clampedX = min(max(-overflow, value.x), size.width + overflow)
        val clampedY = min(max(-overflow, value.y), size.height + overflow)
        return Offset(clampedX, clampedY)
    }
}

@Composable
fun rememberDragFlingController(
    radiusPx: () -> Float,
    containerSize: () -> Size,
    overflowMultiplier: Float,
    velocityScale: Float,
    scope: CoroutineScope,
    decay: DecayAnimationSpec<Offset> = rememberSplineBasedDecay(),
): DragFlingController {
    return remember(radiusPx, containerSize, overflowMultiplier, velocityScale, scope, decay) {
        DragFlingController(
            radiusPx = radiusPx,
            containerSize = containerSize,
            overflowMultiplier = overflowMultiplier,
            velocityScale = velocityScale,
            scope = scope,
            decay = decay,
        )
    }
}
