package com.skul.yuriy.composeplayground.feature.vectorIconShadow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.slider.ThinTrackSlider
import java.util.Locale

@Composable
fun SpreadHaloShadowSection() {
    // State variables for the slider values
    var blurRadius by remember { mutableStateOf(16.dp) }
    var scaleFactor by remember { mutableStateOf(1.2f) }
    var colorAlpha by remember { mutableStateOf(0.5f) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {

        // Blur Radius Slider
        LabeledSlider(
            label = stringResource(R.string.blur_radius_dp),
            value = blurRadius.value,
            onValueChange = { blurRadius = it.dp },
            valueRange = 5f..25f,
            formattedValue = "${blurRadius.value.toInt()} dp" // Pass formatted value with suffix
        )

        // Scale Factor Slider
        LabeledSlider(
            label = stringResource(R.string.scale_factor),
            value = scaleFactor,
            onValueChange = { scaleFactor = it },
            valueRange = 1.2f..3.5f,
            formattedValue = String.format(Locale.US, "%.1f", scaleFactor) // Format to 1 decimal place
        )

        // Alpha Slider
        LabeledSlider(
            label = stringResource(R.string.alpha),
            value = colorAlpha,
            onValueChange = { colorAlpha = it },
            valueRange = 0.1f..1f,
            formattedValue = String.format(Locale.US, "%.1f", colorAlpha) // Format to 1 decimal place
        )

        SpreadShadowBox(
            modifier = Modifier
                .padding(top = 16.dp)
                .size(170.dp)
                .border(
                    BorderStroke(1.dp, color = Color.Green.copy(alpha = 0.4f)),
                    shape = CircleShape
                )
                .background(color = Color.Transparent, shape = CircleShape)
                .clip(CircleShape),
            colorAlpha = colorAlpha,
            blurRadius = blurRadius,
            scaleFactor = scaleFactor,
            imageVector = Icons.Default.Add
        )

        Text(
            text = "Spread Shadow Example",
            color = Color.White,
            modifier = Modifier.padding(vertical = 24.dp),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun SpreadShadowBox(
    modifier: Modifier = Modifier,
    iconTintColor: Color = Color.Green,
    scaleFactor: Float = 1.2f,
    colorAlpha: Float = 0.5f,
    blurRadius: Dp = 16.dp,
    iconSize: Dp = 100.dp,
    imageVector: ImageVector,
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        // Shadow icon, offset dynamically based on the light source (draggable dot) position
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .scale(scaleFactor)
                .blur(radius = blurRadius),
            tint = iconTintColor.copy(alpha = colorAlpha),
        )

        // Main icon in the center
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = imageVector,
            contentDescription = "Add",
            tint = iconTintColor
        )
    }
}


@Composable
fun LabeledSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    formattedValue: String, // New parameter for the formatted value
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formattedValue,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
        ThinTrackSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
        )
    }
}