package com.skul.yuriy.composeplayground.feature.liquidBar.liquid

import androidx.compose.ui.unit.Dp

internal fun resolveHitZoneTopPx(
    containerHeightPx: Int,
    hitZoneHeightPx: Int,
    hitHeight: Dp?,
    interactiveContentPosition: InteractiveContentPosition,
): Int {
    return when {
        hitHeight == null -> 0
        interactiveContentPosition == InteractiveContentPosition.Top -> 0
        else -> (containerHeightPx - hitZoneHeightPx).coerceAtLeast(0)
    }
}
