package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal object FlowTextDefaults {
    val FloatingBoxWidth: Dp = 100.dp
    val FloatingBoxHeight: Dp = 135.dp
    val FloatingBoxTopOffset: Dp = 56.dp
    val FloatingBoxGap: Dp = 16.dp

    fun floatingBoxConfig(
        width: Dp = FloatingBoxWidth,
        height: Dp = FloatingBoxHeight,
        topOffset: Dp = FloatingBoxTopOffset,
        gap: Dp = FloatingBoxGap,
    ): FloatingBoxConfig =
        FloatingBoxConfig(
            width = width,
            height = height,
            topOffset = topOffset,
            gap = gap,
        )
}
