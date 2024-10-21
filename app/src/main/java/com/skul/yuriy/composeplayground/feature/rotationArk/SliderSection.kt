package com.skul.yuriy.composeplayground.feature.rotationArk

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SliderSection(
    label: String,
    currentValue: Dp,
    valueRange: IntRange,
    stepSize: Int = 1,
    onValueChange: (Dp) -> Unit,
    modifier: Modifier
) {
    val steps = valueRange.step(stepSize).map { it.dp }
    val currentIndex = steps.indexOfFirst { it == currentValue }.coerceAtLeast(0)
    val sliderPosition = remember { mutableStateOf(currentIndex.toFloat()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Label at the start
        Text(
            fontSize = 14.sp,
            text = label,
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )

        Slider(
            value = sliderPosition.value,
            onValueChange = { newValue ->
                sliderPosition.value = newValue
                onValueChange(steps[newValue.toInt()])
            },
            valueRange = 0f..(steps.size - 1).toFloat(),
            steps = steps.size - 2,
            modifier = Modifier.weight(3f), // Takes available width for the slider
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTickColor = Color.Red,
                inactiveTickColor = Color.White,
                inactiveTrackColor = Color.Gray,
                activeTrackColor = Color.White,
            )
        )
        // Current value at the end
        Text(
            fontSize = 12.sp,
            text = "${currentValue.value.toInt()}dp",
            modifier = Modifier.padding(start = 16.dp),
            color = Color.Gray
        )
    }
}
