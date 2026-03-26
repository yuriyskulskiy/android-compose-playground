package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun CornerSliderPairRow(
    startCorner: CornerSliderCorner,
    startValue: Float,
    onStartValueChange: (Float) -> Unit,
    endCorner: CornerSliderCorner,
    endValue: Float,
    onEndValueChange: (Float) -> Unit,
    maxValue: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            QuarterArcSlider(
                corner = startCorner,
                value = startValue,
                onValueChange = onStartValueChange,
                maxValue = maxValue
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            QuarterArcSlider(
                corner = endCorner,
                value = endValue,
                onValueChange = onEndValueChange,
                maxValue = maxValue
            )
        }
    }
}
