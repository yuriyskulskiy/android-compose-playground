package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedRectButton.snake.RectSnakeTrackPlacement

@Composable
internal fun BorderToggleButton(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isActive = checked || isPressed
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isActive) Color.White else Color.Gray,
                shape = shape
            )
            .background(
                color = when {
                    isPressed -> Color.White.copy(alpha = 0.15f)
                    else -> Color.Transparent
                },
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(
                if (checked) R.string.rect_snake_border_on else R.string.rect_snake_border_off
            ),
            color = if (checked) Color.White else Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
internal fun SnakePlacementCycleButton(
    placement: RectSnakeTrackPlacement,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = if (isPressed) Color.White else Color.Gray,
                shape = shape
            )
            .background(
                color = if (isPressed) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onNext
            )
            .animateContentSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(placement.toUiLabelRes()),
            color = Color.White
        )
    }
}

@Composable
internal fun ShapeModeToggleButton(
    shapeMode: RectButtonShapeMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = if (isPressed) Color.White else Color.Gray,
                shape = shape
            )
            .background(
                color = if (isPressed) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "Shape",
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .size(
                        width = if (shapeMode == RectButtonShapeMode.CIRCLE) 18.dp else 24.dp,
                        height = 18.dp
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = if (shapeMode == RectButtonShapeMode.CIRCLE) {
                            CircleShape
                        } else {
                            RectangleShape
                        }
                    )
            )
        }
    }
}

@StringRes
private fun RectSnakeTrackPlacement.toUiLabelRes(): Int = when (this) {
    RectSnakeTrackPlacement.INSIDE -> R.string.rect_snake_track_inside
    RectSnakeTrackPlacement.CENTER_ON_EDGE -> R.string.rect_snake_track_on_edge
    RectSnakeTrackPlacement.OUTSIDE -> R.string.rect_snake_track_outside
}

internal fun RectSnakeTrackPlacement.next(): RectSnakeTrackPlacement = when (this) {
    RectSnakeTrackPlacement.INSIDE -> RectSnakeTrackPlacement.CENTER_ON_EDGE
    RectSnakeTrackPlacement.CENTER_ON_EDGE -> RectSnakeTrackPlacement.OUTSIDE
    RectSnakeTrackPlacement.OUTSIDE -> RectSnakeTrackPlacement.INSIDE
}
