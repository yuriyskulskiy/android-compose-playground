package com.skul.yuriy.composeplayground.feature.vectorIconShadow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.math.computeShadowOffset
import com.skul.yuriy.composeplayground.util.regularComponents.RadioTextButton
import com.skul.yuriy.composeplayground.util.slider.satelliteDotDraggable

@Composable
fun DropShadowSection() {
    var shadowOffsetSize by remember { mutableStateOf(8.dp) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        // Offset label and radio buttons
        Text(
            text = stringResource(R.string.shadow_offset_dp),
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .align(alignment = Alignment.Start)
        )

        //Settings block: Row of radio buttons with values 4.dp, 6.dp, 8.dp, and 10.dp
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            listOf(4.dp, 6.dp, 8.dp, 10.dp).forEach { offsetValue ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                ) {

                    RadioTextButton(
                        offsetValue = offsetValue,
                        isSelected = shadowOffsetSize == offsetValue,
                        onClick = { shadowOffsetSize = offsetValue },
                        selectedColor = Color.Green,
                        selectedTextColor = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // DropShadowBox composable with dynamic shadow offset
        DropShadowBox(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(200.dp),
            shadowOffsetSize = shadowOffsetSize,
            circularSliderArcRadius = 100.dp,
            imageVector = Icons.Default.Add
        )
        //section title
        Text(
            text = stringResource(R.string.drop_shadow_example),
            color = Color.White,
            modifier = Modifier.padding(vertical = 24.dp),
            style = MaterialTheme.typography.titleLarge
        )
    }
}


@Composable
fun DropShadowBox(
    modifier: Modifier = Modifier,
    shadowOffsetSize: Dp = 8.dp,
    thumbsRadius: Dp = 8.dp,
    circularSliderArcRadius: Dp,
    iconSize: Dp = 100.dp,
    blurRadius: Dp = 8.dp,
    iconTintColor: Color = Color.Green,
    circularTrackColor: Color = Color.Green.copy(alpha = 0.4f),
    backgroundColor: Color = Color.Transparent,
    imageVector: ImageVector,
) {

    // Angle state for the draggable circle
    val angleState = remember { mutableFloatStateOf(90f) }

    // Calculate shadow offset based on the current angle of the draggable circle
    val shadowOffset = remember(angleState.floatValue, shadowOffsetSize) {
        computeShadowOffset(angleDegrees = angleState.floatValue, radius = shadowOffsetSize)
    }

    Box(
        modifier = modifier
            .drawWithCache {
                val borderThickness = 1.dp.toPx()
                val radiusReduction = thumbsRadius.toPx() * 2
                val adjustedRadius = (size.minDimension / 2) - radiusReduction
                // Calculate adjusted radius to draw the border slightly
                // inside the composable's real edges,
                // creating a small touch slop for dragging interactions.
                onDrawBehind {
                    drawCircle(
                        color = circularTrackColor.copy(alpha = 0.4f),
                        radius = adjustedRadius,
                        style = Stroke(width = borderThickness)
                    )
                }
            }
            .satelliteDotDraggable(
                angleState = angleState,
                circularSliderArcRadius = circularSliderArcRadius,
                dotRadius = thumbsRadius
            )
            .background(color = backgroundColor, shape = CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Shadow icon, offset dynamically based on the light source (draggable dot) position
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .offset(x = shadowOffset.first, y = shadowOffset.second)
                .blur(blurRadius),
            tint = iconTintColor.copy(alpha = 0.9f),
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

