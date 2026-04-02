package com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombus

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class RhombusTextLayoutConfig(
    val lineWidth: Dp,
    val firstLineOffset: Dp,
    val horizontalShiftPerHeight: Float,
    val horizontalPadding: Dp = 24.dp,
    val verticalPadding: Dp = 24.dp,
    val edgeInset: Dp = 8.dp,
)
