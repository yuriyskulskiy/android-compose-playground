package com.skul.yuriy.composeplayground.feature.liquidBar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.InteractiveContentPosition
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.LiquidBox
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.RenderType

@Composable
internal fun LiquidBottomBar(
    destinations: List<LiquidBarDestination>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    screenMode: ScreenMode,
    renderType: RenderType,
    clipContentByWavePath: Boolean,
    liquidContainerHeight: Dp,
    navBarHeight: Dp,
    bottomInset: Dp,
    modifier: Modifier = Modifier,
) {
    LiquidBox(
        contentAlignment = Alignment.BottomStart,
        modifier = modifier
            .fillMaxWidth()
            .height(liquidContainerHeight),
        bg = Color.Transparent,
        plotWidth = 0f,
        renderType = renderType,
        clipContentByWavePath = clipContentByWavePath,
        waveColor = Color.Black,
        hitHeight = navBarHeight + bottomInset,
        interactiveContentPosition = InteractiveContentPosition.Bottom,
    ) {
        val useCanvasDifference = screenMode == ScreenMode.Canvas && !clipContentByWavePath

        NavigationBar(
            containerColor = Color.Transparent
        ) {
            destinations.forEachIndexed { index, destination ->
                val isSelected = selectedIndex == index
                val interactionSource = remember { MutableInteractionSource() }
                val onClick = remember(index, onSelectedIndexChange) {
                    { onSelectedIndexChange(index) }
                }
                val isPressed by interactionSource.collectIsPressedAsState()
                val pressedBgAlpha by animateFloatAsState(
                    targetValue = if (!useCanvasDifference && isPressed) 0.15f else 0f,
                    animationSpec = tween(durationMillis = 180),
                    label = "BottomBarPressedBgAlpha"
                )
                val pressedBorderAlpha by animateFloatAsState(
                    targetValue = if (!useCanvasDifference && isPressed) 1f else 0f,
                    animationSpec = tween(durationMillis = 180),
                    label = "BottomBarPressedBorderAlpha"
                )
                val itemColor = if (isSelected) Color.Red else Color.White
                val itemModifier = if (useCanvasDifference && !isSelected) {
                    Modifier.invertByDifferenceBlend()
                } else {
                    Modifier
                }
                val navItemModifier = if (!useCanvasDifference) {
                    Modifier
                        .background(
                            color = Color.White.copy(alpha = pressedBgAlpha),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = pressedBorderAlpha),
                            shape = RoundedCornerShape(16.dp)
                        )
                } else {
                    Modifier
                }

                CompositionLocalProvider(
                    LocalRippleConfiguration provides null
                ) {
                    NavigationBarItem(
                        modifier = navItemModifier,
                        selected = isSelected,
                        onClick = onClick,
                        interactionSource = interactionSource,
                        icon = {
                            androidx.compose.material3.Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                tint = itemColor,
                                modifier = itemModifier
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                color = itemColor,
                                modifier = itemModifier
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Unspecified,
                            unselectedIconColor = Color.Unspecified,
                            selectedTextColor = Color.Unspecified,
                            unselectedTextColor = Color.Unspecified,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

internal data class LiquidBarDestination(
    val label: String,
    val icon: ImageVector,
)
