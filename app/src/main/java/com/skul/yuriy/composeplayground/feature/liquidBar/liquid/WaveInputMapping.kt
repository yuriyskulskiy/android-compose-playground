package com.skul.yuriy.composeplayground.feature.liquidBar.liquid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.unit.IntSize

internal fun injectWaveAt(
    localPos: Offset,
    containerSize: IntSize,
    hitZoneSize: IntSize,
    interactiveContentPosition: InteractiveContentPosition,
    sim: Wave_1D,
): Boolean {
    if (containerSize.width <= 0 || containerSize.height <= 0) return false
    if (hitZoneSize.width <= 0 || hitZoneSize.height <= 0) return false

    val zoneTopInRoot = when (interactiveContentPosition) {
        InteractiveContentPosition.Top -> 0f
        InteractiveContentPosition.Bottom -> (containerSize.height - hitZoneSize.height).toFloat()
    }
    val rootPos = localPos + Offset(0f, zoneTopInRoot)

    val cx = (rootPos.x / containerSize.width.toFloat()).coerceIn(0f, 1f)
    val cyTopDown = (rootPos.y / containerSize.height.toFloat()).coerceIn(0f, 1f)
    val cyBottomUp = 1f - cyTopDown
    val cyForInjection = when (interactiveContentPosition) {
        InteractiveContentPosition.Top -> cyTopDown
        InteractiveContentPosition.Bottom -> cyBottomUp
    }

    sim.inject(cx, cyForInjection.coerceIn(0f, 1f))
    return true
}

internal suspend fun AwaitPointerEventScope.listenWaveDragEvents(
    dragThrottleMs: Long,
    containerSizeProvider: () -> IntSize,
    hitZoneSizeProvider: () -> IntSize,
    interactiveContentPosition: InteractiveContentPosition,
    sim: Wave_1D,
    onWaveInjected: () -> Unit,
) {
    var lastInjectMs = 0L
    while (true) {
        val event = awaitPointerEvent(pass = PointerEventPass.Final)
        val pressed = event.changes.firstOrNull { it.pressed } ?: continue
        val nowMs = pressed.uptimeMillis
        if (nowMs - lastInjectMs >= dragThrottleMs) {
            val injected = injectWaveAt(
                localPos = pressed.position,
                containerSize = containerSizeProvider(),
                hitZoneSize = hitZoneSizeProvider(),
                interactiveContentPosition = interactiveContentPosition,
                sim = sim
            )
            if (injected) {
                lastInjectMs = nowMs
                onWaveInjected()
            }
        }
    }
}
