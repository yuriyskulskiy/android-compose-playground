package com.skul.yuriy.composeplayground.feature.sensorRotation.text

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.scroll.rememberRotationAwareFlingBehavior

/**
 * Entry point for the future custom text implementation that will layout content
 * inside sensor-rotation driven shapes instead of a regular rectangular paragraph.
 */
@Composable
internal fun RotationShapeText(
    text: String,
    angleDegrees: Float,
    modifier: Modifier = Modifier,
) {
    val flingBehavior = rememberRotationAwareFlingBehavior(angleDegrees)
    Text(
        text = text,
        modifier = modifier
            .verticalScroll(
                state = rememberScrollState(),
                flingBehavior = flingBehavior,
            )
            .padding(horizontal = 24.dp, vertical = 24.dp),
        overflow = TextOverflow.Clip,
    )
}
