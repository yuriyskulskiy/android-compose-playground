package com.skul.yuriy.composeplayground.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun cornerRedLinearGradient(): Brush = Brush.linearGradient(
    colorStops = gradientColorStops.toTypedArray(),
    start = Offset(0f, 0f),
    end = Offset(
        Float.POSITIVE_INFINITY,
        Float.POSITIVE_INFINITY
    )
)

private val gradientColorStops = listOf(
    0.0f to Color(0xFF8B0000),
    0.20f to Color(0xFF000000),
    0.40f to Color(0xFF8B0000),
    0.50f to Color(0xFF000000),
    0.60f to Color(0xFF602AA3),
    0.75f to Color(0xFF000000),
    1.0f to Color(0xFF8B0000)
)

fun cornerRedLinearGradient2(): Brush = Brush.linearGradient(
    colorStops = gradientColorStops2.toTypedArray(),
    start = Offset(0f, 0f),
    end = Offset(
        Float.POSITIVE_INFINITY,
        Float.POSITIVE_INFINITY
    )
)

private val gradientColorStops2 = listOf(
    0.0f to Color(0xFF4B0000),
    0.10f to Color(0xFF4B0000),
    0.15f to Color(0xFF000000),
    0.25f to Color(0xFF4B0000),  // Darker red for better contrast with white text
    0.55f to Color(0xFF000000), // Black section to provide contrast
    0.83f to Color(0xFF000000), // Larger black section to frame the red
    0.93f to Color(0xFF4B0000), // Larger black section to frame the red
    1.0f to Color(0xFF4B0000)   // Darker red at the end
)