package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.cornerDarkRedLinearGradient2
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar
import com.skul.yuriy.composeplayground.feature.animatedRectButton.snake.RectSnakeTrackPlacement

@Composable
fun AnimatedRectBtnScreen() {
    val navBackStack = LocalNavBackStack.current
    var showDebugTrack by rememberSaveable { mutableStateOf(false) }
    var trackPlacement by rememberSaveable { mutableStateOf(RectSnakeTrackPlacement.OUTSIDE) }
    var shapeMode by rememberSaveable { mutableStateOf(RectButtonShapeMode.CIRCLE) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = cornerDarkRedLinearGradient2())
            .navigationBarsPadding()
    ) {

        CustomTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            onNavUp = { navBackStack.navigateUp() },
            containerColor = Color.Transparent,
            title = stringResource(R.string.animated_rect_button)
        )

        AnimatedRectButtonSettingsBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            showDebugTrack = showDebugTrack,
            onToggleDebugTrack = { showDebugTrack = !showDebugTrack },
            shapeMode = shapeMode,
            onToggleShapeMode = { shapeMode = shapeMode.toggle() },
            trackPlacement = trackPlacement,
            onCycleTrackPlacement = { trackPlacement = trackPlacement.next() }
        )

        AnimatedRectButtonScreenContent(
            modifier = Modifier.fillMaxSize(),
            showDebugTrack = showDebugTrack,
            trackPlacement = trackPlacement,
            shapeMode = shapeMode
        )
    }
}
