package com.skul.yuriy.composeplayground.feature.liquidBar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.InteractiveContentPosition
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.LiquidBox
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.RenderType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LiquidTopBar(
    screenMode: ScreenMode,
    renderType: RenderType,
    clipContentByWavePath: Boolean,
    topLiquidContainerHeight: Dp,
    topBarHitHeight: Dp,
    topInset: Dp,
    onePixelDp: Dp,
    topSwitchRowHeight: Dp,
    onNavUp: () -> Unit,
    onModeSelected: (ScreenMode) -> Unit,
    onClipToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val useCanvasDifference = screenMode == ScreenMode.Canvas && !clipContentByWavePath

    LiquidBox(
        modifier = modifier
            .fillMaxWidth()
            .height(topLiquidContainerHeight),
        contentAlignment = Alignment.TopStart,
        waveColor = Color.Black,
        plotWidth = 0f,
        bg = Color.Transparent,
        renderType = renderType,
        clipContentByWavePath = clipContentByWavePath,
        hitHeight = topBarHitHeight,
        interactiveContentPosition = InteractiveContentPosition.Top,
    ) {
        val topBarContentModifier = if (useCanvasDifference) {
            Modifier.invertByDifferenceBlend()
        } else {
            Modifier
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topInset)
        ) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text(
                        text = stringResource(R.string.pde_liquid_bar_title),
                        color = Color.White,
                        modifier = topBarContentModifier
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                            tint = Color.White,
                            modifier = topBarContentModifier
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(visible = screenMode == ScreenMode.Canvas) {
                        IconButton(onClick = onClipToggle) {
                            Icon(
                                imageVector = Icons.Filled.ContentCut,
                                contentDescription = if (clipContentByWavePath) {
                                    stringResource(R.string.clip_wave_on)
                                } else {
                                    stringResource(R.string.clip_wave_off)
                                },
                                tint = if (clipContentByWavePath) Color.Red else Color.Gray
                            )
                        }
                    }
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(onePixelDp)
                    .background(Color.White)
            )
            RenderTypeSwitch(
                selectedMode = screenMode,
                onModeSelected = onModeSelected,
                applyDifference = useCanvasDifference,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topSwitchRowHeight)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenderTypeSwitch(
    selectedMode: ScreenMode,
    onModeSelected: (ScreenMode) -> Unit,
    applyDifference: Boolean,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = when (selectedMode) {
        ScreenMode.Canvas -> 0
        ScreenMode.Agsl -> 1
        ScreenMode.AgslCanvas -> 2
    }

    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        PrimaryTabRow(
            selectedTabIndex = selectedIndex,
            modifier = modifier,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            divider = {},
            indicator = {
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(selectedIndex)
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Red,
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
        ) {
            renderTabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex
                val tabTextModifier = if (!isSelected && applyDifference) {
                    Modifier.invertByDifferenceBlend()
                } else {
                    Modifier
                }
                val onClick = remember(tab.mode, onModeSelected) {
                    { onModeSelected(tab.mode) }
                }

                Tab(
                    selected = isSelected,
                    onClick = onClick,
                    text = {
                        Text(
                            text = tab.label,
                            color = if (isSelected) Color.Red else Color.Gray,
                            modifier = tabTextModifier
                        )
                    }
                )
            }
        }
    }
}

private data class RenderTab(
    val mode: ScreenMode,
    val label: String,
)

private val renderTabs = listOf(
    RenderTab(mode = ScreenMode.Canvas, label = "CNVS"),
    RenderTab(mode = ScreenMode.Agsl, label = "AGSL"),
    RenderTab(mode = ScreenMode.AgslCanvas, label = "A-CNVS")
)
