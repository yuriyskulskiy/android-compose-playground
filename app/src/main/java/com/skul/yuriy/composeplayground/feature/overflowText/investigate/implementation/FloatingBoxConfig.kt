package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class FloatingBoxConfig(
    val width: Dp,
    val height: Dp,
    val topOffset: Dp = 0.dp,
    val gap: Dp = 0.dp,
)
