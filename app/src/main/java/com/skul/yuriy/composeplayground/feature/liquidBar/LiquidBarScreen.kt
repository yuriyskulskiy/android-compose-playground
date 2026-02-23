package com.skul.yuriy.composeplayground.feature.liquidBar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.InteractiveContentPosition
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.LiquidBox
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.RenderType
import com.skul.yuriy.composeplayground.navigation.navigateUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidBarScreen(
    modifier: Modifier = Modifier
) {
    val navBackStack = LocalNavBackStack.current
    val destinations = liquidBarDestinations()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var screenMode by rememberSaveable { mutableStateOf(ScreenMode.Canvas) }
    val renderType = screenMode.toRenderType()
    val baseText = stringResource(R.string.very_long_mock_text).trimIndent()
    val contentText = "$baseText\n\n$baseText"

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val navBarHeight = 80.dp
    val topBarHeight = TopAppBarDefaults.TopAppBarExpandedHeight
    val topBarHitHeight = topInset + topBarHeight
    val topLiquidContainerHeight = topBarHitHeight * 2
    val liquidContainerHeight = navBarHeight * 2 + bottomInset * 2
    val contentPadding = PaddingValues(
        top = topBarHitHeight,
        bottom = bottomInset + navBarHeight
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LiquidBarContent(
            screenMode = screenMode,
            paddingValues = contentPadding,
            contentText = contentText,
            modifier = Modifier
                .fillMaxSize()
        )


        LiquidBox(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(liquidContainerHeight),
            bg = Color.Transparent,
            plotWidth = 1f,
            renderType = renderType,
            waveColor = Color.Black,
            hitHeight = navBarHeight + bottomInset,
            interactiveContentPosition = InteractiveContentPosition.Bottom,
        ) {
            NavigationBar(
                containerColor = Color.Transparent
            ) {
                destinations.forEachIndexed { index, destination ->
                    val isSelected = selectedIndex == index
                    val isCanvasMode = screenMode == ScreenMode.Canvas
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val pressedBgAlpha by animateFloatAsState(
                        targetValue = if (!isCanvasMode && isPressed) 0.15f else 0f,
                        animationSpec = tween(durationMillis = 180),
                        label = "BottomBarPressedBgAlpha"
                    )
                    val pressedBorderAlpha by animateFloatAsState(
                        targetValue = if (!isCanvasMode && isPressed) 1f else 0f,
                        animationSpec = tween(durationMillis = 180),
                        label = "BottomBarPressedBorderAlpha"
                    )
                    val itemColor = if (isSelected) Color.Red else Color.White
                    val itemModifier = if (isCanvasMode && !isSelected) {
                        Modifier
                            .invertOverBackgroundInLiquidBar()
                    } else {
                        Modifier
                    }
                    val navItemModifier = if (!isCanvasMode) {
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
                    val navItemContent: @Composable () -> Unit = {
                        NavigationBarItem(
                            modifier = navItemModifier,
                            selected = isSelected,
                            onClick = { selectedIndex = index },
                            interactionSource = interactionSource,
                            icon = {
                                Icon(
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

                    CompositionLocalProvider(
                        LocalRippleConfiguration provides null
                    ) {
                        navItemContent()
                    }
                }
            }
        }



        LiquidBox(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(topLiquidContainerHeight),
            contentAlignment = Alignment.TopStart,
            waveColor = Color.Black,
            plotWidth = 0f,
            bg = Color.Transparent,
            renderType = renderType,
            hitHeight = topBarHitHeight,
            interactiveContentPosition = InteractiveContentPosition.Top,
        ) {
            val topBarContentModifier = when (screenMode) {
                ScreenMode.Canvas -> Modifier.invertOverBackgroundInLiquidBar()
                ScreenMode.Agsl -> Modifier
            }

            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text(
                        text = stringResource(R.string.liquid_bar),
                        color = Color.White,
                        modifier = topBarContentModifier
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navBackStack.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                            tint = Color.White,
                            modifier = topBarContentModifier
                        )
                    }
                },
                actions = {
                    RenderTypeSwitch(
                        selectedMode = screenMode,
                        onModeSelected = { screenMode = it },
                        applyDifference = screenMode == ScreenMode.Canvas
                    )
                }
            )
        }
    }
}


@Composable
private fun liquidBarDestinations(): List<LiquidBarDestination> {
    return listOf(
        LiquidBarDestination(
            label = stringResource(R.string.home),
            icon = Icons.Filled.Home
        ),
        LiquidBarDestination(
            label = stringResource(R.string.shopping_cart),
            icon = Icons.Filled.ShoppingCart
        ),
        LiquidBarDestination(
            label = stringResource(R.string.search),
            icon = Icons.Filled.Search
        ),
        LiquidBarDestination(
            label = stringResource(R.string.profile),
            icon = Icons.Filled.Person
        ),
        LiquidBarDestination(
            label = stringResource(R.string.settings),
            icon = Icons.Filled.Settings
        )
    )
}

private data class LiquidBarDestination(
    val label: String,
    val icon: ImageVector,
)

@Composable
private fun RenderTypeSwitch(
    selectedMode: ScreenMode,
    onModeSelected: (ScreenMode) -> Unit,
    applyDifference: Boolean,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        Row(
            modifier = modifier.padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TopModeChip(
                label = "CNVS",
                selected = selectedMode == ScreenMode.Canvas,
                onClick = { onModeSelected(ScreenMode.Canvas) },
                applyDifference = applyDifference
            )
            TopModeChip(
                label = "AGSL",
                selected = selectedMode == ScreenMode.Agsl,
                onClick = { onModeSelected(ScreenMode.Agsl) },
                applyDifference = applyDifference
            )
        }
    }
}

@Composable
private fun TopModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    applyDifference: Boolean,
) {
    val textColor = if (selected) Color.Red else Color.White.copy(alpha = 0.65f)
    val textModifier = if (!selected && applyDifference) {
        Modifier.invertOverBackgroundInLiquidBar()
    } else {
        Modifier
    }

    Text(
        text = label,
        color = textColor,
        modifier = textModifier
            .then(
                if (selected) {
                    Modifier
                        .border(width = 1.dp, color = Color.Red, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                } else {
                    Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                }
            )
            .clickable(onClick = onClick)
    )
}

private fun Modifier.invertOverBackgroundInLiquidBar(): Modifier = graphicsLayer {
    compositingStrategy = CompositingStrategy.Offscreen
    blendMode = BlendMode.Difference
}

private fun ScreenMode.toRenderType(): RenderType = when (this) {
    ScreenMode.Canvas -> RenderType.CANVAS
    ScreenMode.Agsl -> RenderType.AGSL
}
