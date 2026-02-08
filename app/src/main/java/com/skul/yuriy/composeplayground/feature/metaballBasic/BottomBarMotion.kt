package com.skul.yuriy.composeplayground.feature.metaballBasic

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val BottomBarAnimMs = 260

/**
 * Bottom bar motion helper to slide the bar in/out while keeping the content inset
 * animated. Using AnimatedVisibility for the bar makes the content jump because
 * Scaffold padding changes abruptly; this keeps it smooth.
 */
internal data class BottomBarMotion(
    val contentBottomInset: Dp,
    val barTranslationY: Dp,
    val barHeight: Dp,
)

@Composable
internal fun rememberBottomBarMotion(
    visible: Boolean,
    barHeight: Dp,
    durationMs: Int = BottomBarAnimMs,
    onHidden: () -> Unit,
): BottomBarMotion {
    val inset by animateDpAsState(
        targetValue = if (visible) barHeight else 0.dp,
        animationSpec = tween(durationMs),
        label = "contentInset"
    )

    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else barHeight,
        animationSpec = tween(durationMs),
        label = "barOffsetY",
        finishedListener = {
            if (!visible && barHeight > 0.dp) {
                onHidden()
            }
        }
    )

    return BottomBarMotion(
        contentBottomInset = inset,
        barTranslationY = offsetY,
        barHeight = barHeight,
    )
}
