package com.skul.yuriy.composeplayground.feature.customAlphaBlur

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.BlurKernelQuality
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.BlurPageKey
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.CustomBlurMode

private const val MinWeight = 0.0001f
private const val WeightVisibleThreshold = 0.001f
private const val ButtonsAnimationDurationMs = 350
private val BlurModeTabs = listOf(
    CustomBlurMode.Native to "Native",
    CustomBlurMode.AglslAlphaLinear to "AGSL Linear",
    CustomBlurMode.AgslAlphaGaussian to "AGSL Gauss",
)
private val BlurKernelQualityTabs = listOf(
    BlurKernelQuality.Taps17,
    BlurKernelQuality.Taps61,
    BlurKernelQuality.Taps101,
)

@Composable
fun CustomBlurBottomBar(
    selectedPage: BlurPageKey,
    selectedMode: CustomBlurMode,
    onModeSelected: (CustomBlurMode) -> Unit,
    selectedKernelQuality: BlurKernelQuality,
    onKernelQualitySelected: (BlurKernelQuality) -> Unit,
    showBackward: Boolean,
    showForward: Boolean,
    onBackward: () -> Unit,
    onForward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bothVisible = showBackward && showForward

    val backwardWeight by animateFloatAsState(
        targetValue = if (showBackward) 1f else MinWeight,
        animationSpec = tween(durationMillis = ButtonsAnimationDurationMs),
        label = "BackwardWeight"
    )
    val forwardWeight by animateFloatAsState(
        targetValue = if (showForward) 1f else MinWeight,
        animationSpec = tween(durationMillis = ButtonsAnimationDurationMs),
        label = "ForwardWeight"
    )
    val spacing by animateDpAsState(
        targetValue = if (bothVisible) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = ButtonsAnimationDurationMs),
        label = "ButtonsSpacing"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedContent(
                    targetState = selectedPage == BlurPageKey.KernelQuality,
                    modifier = Modifier.fillMaxWidth(),
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 80)) +
                            slideInVertically(
                                initialOffsetY = { it / 4 },
                                animationSpec = tween(220, delayMillis = 80)
                            )) togetherWith
                            (fadeOut(animationSpec = tween(140)) +
                                slideOutVertically(
                                    targetOffsetY = { -it / 5 },
                                    animationSpec = tween(140)
                                )) using
                            SizeTransform(
                                clip = false,
                                sizeAnimationSpec = { _, _ -> tween(350) }
                            )
                    },
                    label = "BottomBarTopTabsSwitch"
                ) { isKernelQualityPage ->
                    if (isKernelQualityPage) {
                        KernelQualityTabs(
                            selectedQuality = selectedKernelQuality,
                            onQualitySelected = onKernelQualitySelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        ModeTabs(
                            selectedMode = selectedMode,
                            onModeSelected = onModeSelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(backwardWeight),
                    contentAlignment = Alignment.Center
                ) {
                    if (showBackward || backwardWeight > WeightVisibleThreshold) {
                        DarkBarOutlinedButton(
                            label = "Backward",
                            onClick = onBackward,
                            enabled = showBackward,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(forwardWeight),
                    contentAlignment = Alignment.Center
                ) {
                    if (showForward || forwardWeight > WeightVisibleThreshold) {
                        DarkBarOutlinedButton(
                            label = "Forward",
                            onClick = onForward,
                            enabled = showForward,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeTabs(
    selectedMode: CustomBlurMode,
    onModeSelected: (CustomBlurMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = BlurModeTabs.indexOfFirst { it.first == selectedMode }
        .takeIf { it >= 0 }
        ?: 0

    PrimaryTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        divider = {},
        indicator = {
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(selectedIndex, matchContentSize = false)
                    .padding(vertical = 2.dp)
                    .fillMaxHeight()
                    .background(
                        color = Color.White.copy(alpha = 0.22f),
                        shape = CircleShape
                    )
            )
        }
    ) {
        BlurModeTabs.forEachIndexed { index, (mode, label) ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onModeSelected(mode) },
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .clip(CircleShape),
                text = {
                    Text(
                        text = label,
                        color = if (selectedIndex == index) Color.White else Color.Gray,
                        fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
private fun KernelQualityTabs(
    selectedQuality: BlurKernelQuality,
    onQualitySelected: (BlurKernelQuality) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = BlurKernelQualityTabs.indexOf(selectedQuality)
        .takeIf { it >= 0 }
        ?: 0

    PrimaryTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        divider = {},
        indicator = {
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(selectedIndex, matchContentSize = false)
                    .padding(vertical = 2.dp)
                    .fillMaxHeight()
                    .background(
                        color = Color.White.copy(alpha = 0.22f),
                        shape = CircleShape
                    )
            )
        }
    ) {
        BlurKernelQualityTabs.forEachIndexed { index, quality ->
            val label = quality.toUiLabel()
            Tab(
                selected = selectedIndex == index,
                onClick = { onQualitySelected(quality) },
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .clip(CircleShape),
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label.title,
                            color = if (selectedIndex == index) Color.White else Color.Gray,
                            fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                        Text(
                            text = label.subtitle,
                            color = if (selectedIndex == index) Color.White else Color.Gray,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            )
        }
    }
}
