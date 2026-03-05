package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.cornerRedLinearGradient2
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar
import com.skul.yuriy.composeplayground.util.shadowborder.RectSnakeTrackPlacement

@Composable
fun AnimatedRectBtnScreen() {
    val navBackStack = LocalNavBackStack.current
    var showDebugTrack by remember { mutableStateOf(false) }
    var trackPlacement by remember { mutableStateOf(RectSnakeTrackPlacement.OUTSIDE) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = cornerRedLinearGradient2())
            .navigationBarsPadding()
    ) {

        CustomTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            onNavUp = { navBackStack.navigateUp() },
            containerColor = Color.Transparent,
            title = stringResource(R.string.animated_rect_button)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BorderToggleButton(
                checked = showDebugTrack,
                onToggle = { showDebugTrack = !showDebugTrack },
                modifier = Modifier.align(Alignment.TopStart)
            )

            SnakePlacementCycleButton(
                placement = trackPlacement,
                onNext = { trackPlacement = trackPlacement.next() },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }

        AnimatedRectButtonScreenContent(
            modifier = Modifier.fillMaxSize(),
            showDebugTrack = showDebugTrack,
            trackPlacement = trackPlacement
        )
    }
}

@Composable
private fun BorderToggleButton(
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
            text = if (checked) "Border ON" else "Border OFF",
            color = if (checked) Color.White else Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SnakePlacementCycleButton(
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
            text = placement.toUiLabel(),
            color = Color.White
        )
    }
}

private fun RectSnakeTrackPlacement.toUiLabel(): String = when (this) {
    RectSnakeTrackPlacement.INSIDE -> "Inside"
    RectSnakeTrackPlacement.CENTER_ON_EDGE -> "On edge"
    RectSnakeTrackPlacement.OUTSIDE -> "Outside"
}

private fun RectSnakeTrackPlacement.next(): RectSnakeTrackPlacement = when (this) {
    RectSnakeTrackPlacement.INSIDE -> RectSnakeTrackPlacement.CENTER_ON_EDGE
    RectSnakeTrackPlacement.CENTER_ON_EDGE -> RectSnakeTrackPlacement.OUTSIDE
    RectSnakeTrackPlacement.OUTSIDE -> RectSnakeTrackPlacement.INSIDE
}
