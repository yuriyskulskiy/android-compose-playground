package com.skul.yuriy.composeplayground.feature.metaballEdgesAdvanced

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MetaballEdgeAdvancedSettingsState(
    val lineHeightMultiplier: Float = 1.55f,
    val thresholdPercent: Int = 20,
    val blurRadius: Dp = 32.dp,
    val textSize: TextUnit = 20.sp,
)

enum class MetaballEdgeAdvancedMode(
    val presetSettings: MetaballEdgeAdvancedSettingsState,
) {
    Gooey(
        presetSettings = MetaballEdgeAdvancedSettingsState(
            thresholdPercent = 25,
            blurRadius = 10.dp,
            textSize = 20.sp,
        )
    ),
    Melt(
        presetSettings = MetaballEdgeAdvancedSettingsState()
    ),
}
