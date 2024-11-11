package com.skul.yuriy.composeplayground.util.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThinTrackSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    trackHeight: Dp = 4.dp, // Set custom thickness here
    activeTrackColor: Color = Color.Green,
    inactiveTrackColor: Color = Color.Green.copy(alpha = 0.3f)
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.Transparent, // Make transparent to avoid default track
            inactiveTrackColor = Color.Transparent // Make transparent to avoid default track
        ),
        track = {
            // Calculate the proportion of the active track based on the value and range
            val progress = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .background(inactiveTrackColor) // Full track as inactive color
            ) {
                // Active track portion (left of the thumb)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress) // Adjust width based on progress
                        .background(activeTrackColor)
                )
            }
        }
    )
}