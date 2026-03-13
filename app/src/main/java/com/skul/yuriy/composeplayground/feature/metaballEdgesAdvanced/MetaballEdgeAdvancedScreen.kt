package com.skul.yuriy.composeplayground.feature.metaballEdgesAdvanced

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.navigation.navigateUp

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MetaballEdgeAdvancedScreen(
    modifier: Modifier = Modifier,
) {
    val navBackStack = LocalNavBackStack.current
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    var selectedMode by remember { mutableStateOf(MetaballEdgeAdvancedMode.Gooey) }
    var currentSettings by remember { mutableStateOf(selectedMode.presetSettings) }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            MetaballEdgeAdvancedTopBar(
                selectedMode = selectedMode,
                onBackClick = { navBackStack.navigateUp() },
                onModeSelected = { mode ->
                    selectedMode = mode
                    currentSettings = mode.presetSettings
                },
            )
        },
        bottomBar = if (isPortrait) {
            {
                MetaballEdgeAdvancedBottomBar(
                    settings = currentSettings,
                    onBlurRadiusDecrease = {
                        currentSettings = currentSettings.copy(
                            blurRadius = currentSettings.blurRadius.previousBlurStep(),
                        )
                    },
                    onBlurRadiusIncrease = {
                        currentSettings = currentSettings.copy(
                            blurRadius = currentSettings.blurRadius.nextBlurStep(),
                        )
                    },
                    onThresholdDecrease = {
                        currentSettings = currentSettings.copy(
                            thresholdPercent = currentSettings.thresholdPercent.previousThresholdStep(),
                        )
                    },
                    onThresholdIncrease = {
                        currentSettings = currentSettings.copy(
                            thresholdPercent = currentSettings.thresholdPercent.nextThresholdStep(),
                        )
                    },
                    onTextSizeDecrease = {
                        currentSettings = currentSettings.copy(
                            textSize = currentSettings.textSize.previousTextSizeStep(),
                        )
                    },
                    onTextSizeIncrease = {
                        currentSettings = currentSettings.copy(
                            textSize = currentSettings.textSize.nextTextSizeStep(),
                        )
                    },
                )
            }
        } else {
            {}
        },
    ) { paddingValues ->
        MetaballEdgeAdvancedContent(
            modifier = Modifier
                .padding(paddingValues.withTopOverlap(0.dp)),
            settings = currentSettings,
            showGradientEdges = selectedMode == MetaballEdgeAdvancedMode.Gooey,
        )
    }
}

private fun PaddingValues.withTopOverlap(overlap: Dp): PaddingValues = PaddingValues(
    start = calculateStartPadding(LayoutDirection.Ltr),
    top = (calculateTopPadding() - overlap).coerceAtLeast(0.dp),
    end = calculateEndPadding(LayoutDirection.Ltr),
    bottom = calculateBottomPadding(),
)
