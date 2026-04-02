package com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombustext

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class RhombusTextLayoutConfig(
    val lineWidth: Dp,
    val firstLineOffset: Dp,
    val horizontalShiftPerHeight: Float,
    val contentTopInset: Dp = 0.dp,
    val scrollOffset: Dp = 0.dp,
    val horizontalPadding: Dp = 24.dp,
    val verticalPadding: Dp = 24.dp,
    val edgeInset: Dp = 8.dp,
)
