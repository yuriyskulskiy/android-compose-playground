package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var showDebugTrack by remember { mutableStateOf(true) }
    var trackPlacement by remember { mutableStateOf(RectSnakeTrackPlacement.CENTER_ON_EDGE) }
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

            SnakePlacementSelector(
                selected = trackPlacement,
                onSelected = { trackPlacement = it },
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
private fun SnakePlacementSelector(
    selected: RectSnakeTrackPlacement,
    onSelected: (RectSnakeTrackPlacement) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        RectSnakeTrackPlacement.INSIDE to "Inside",
        RectSnakeTrackPlacement.CENTER_ON_EDGE to "On edge",
        RectSnakeTrackPlacement.OUTSIDE to "Outside"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { (placement, label) ->
            val interactionSource = remember(placement) { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val isSelected = selected == placement
            val rowShape = RoundedCornerShape(8.dp)
            Row(
                modifier = Modifier
                    .clip(rowShape)
                    .border(
                        width = 1.dp,
                        color = if (isSelected || isPressed) Color.White else Color.Gray,
                        shape = rowShape
                    )
                    .background(
                        color = if (isPressed) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                        shape = rowShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onSelected(placement) }
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f)
                )
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.LightGray
                        )
                    )
                }
            }
        }
    }
}
