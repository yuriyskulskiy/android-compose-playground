package com.skul.yuriy.composeplayground.feature.metaballEdgesAdvanced

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        navigationIconContentColor = Color.White,
                        titleContentColor = Color.White,
                        containerColor = Color.Black,
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navBackStack.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back),
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.metaball_edge_advanced))
                    },
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(14.dp),
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    MetaballEdgeAdvancedModeButton(
                        title = "Gooey",
                        selected = selectedMode == MetaballEdgeAdvancedMode.Gooey,
                        onClick = {
                            selectedMode = MetaballEdgeAdvancedMode.Gooey
                            currentSettings = selectedMode.presetSettings
                        },
                        modifier = Modifier.weight(1f),
                    )
                    MetaballEdgeAdvancedModeButton(
                        title = "Melt",
                        selected = selectedMode == MetaballEdgeAdvancedMode.Melt,
                        onClick = {
                            selectedMode = MetaballEdgeAdvancedMode.Melt
                            currentSettings = selectedMode.presetSettings
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
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

@Composable
private fun MetaballEdgeAdvancedModeButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.background(
            color = if (selected) Color.White else Color.Transparent,
            shape = RoundedCornerShape(10.dp),
        ),
    ) {
        Text(
            text = title,
            color = if (selected) Color.Black else Color.White,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
