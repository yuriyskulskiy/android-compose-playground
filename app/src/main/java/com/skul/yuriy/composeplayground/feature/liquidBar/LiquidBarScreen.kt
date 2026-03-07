package com.skul.yuriy.composeplayground.feature.liquidBar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.RenderType
import com.skul.yuriy.composeplayground.navigation.navigateUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidBarScreen(
    modifier: Modifier = Modifier
) {
    val navBackStack = LocalNavBackStack.current
    val destinations = rememberLiquidBarDestinations()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var screenMode by rememberSaveable { mutableStateOf(ScreenMode.Canvas) }
    val renderType = screenMode.toRenderType()
    val baseText = stringResource(R.string.very_long_mock_text).trimIndent()
    val contentText = "$baseText\n\n$baseText"

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val navBarHeight = 80.dp
    val topBarHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight
    val onePixelDp = with(LocalDensity.current) { (1f / density).dp }
    val topSwitchRowHeight = 48.dp
    val topBarHitHeight = topInset + topBarHeight + onePixelDp + topSwitchRowHeight
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

        LiquidBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            destinations = destinations,
            selectedIndex = selectedIndex,
            onSelectedIndexChange = { selectedIndex = it },
            screenMode = screenMode,
            renderType = renderType,
            liquidContainerHeight = liquidContainerHeight,
            navBarHeight = navBarHeight,
            bottomInset = bottomInset
        )


        LiquidTopBar(
            modifier = Modifier
                .align(Alignment.TopCenter),
            screenMode = screenMode,
            renderType = renderType,
            topLiquidContainerHeight = topLiquidContainerHeight,
            topBarHitHeight = topBarHitHeight,
            topInset = topInset,
            onePixelDp = onePixelDp,
            topSwitchRowHeight = topSwitchRowHeight,
            onNavUp = { navBackStack.navigateUp() },
            onModeSelected = { screenMode = it }
        )
    }
}

@Composable
private fun rememberLiquidBarDestinations(): List<LiquidBarDestination> {
    val home = stringResource(R.string.home)
    val shoppingCart = stringResource(R.string.shopping_cart)
    val search = stringResource(R.string.search)
    val profile = stringResource(R.string.profile)
    val settings = stringResource(R.string.settings)

    return remember(home, shoppingCart, search, profile, settings) {
        listOf(
            LiquidBarDestination(label = home, icon = Icons.Filled.Home),
            LiquidBarDestination(label = shoppingCart, icon = Icons.Filled.ShoppingCart),
            LiquidBarDestination(label = search, icon = Icons.Filled.Search),
            LiquidBarDestination(label = profile, icon = Icons.Filled.Person),
            LiquidBarDestination(label = settings, icon = Icons.Filled.Settings),
        )
    }
}

private fun ScreenMode.toRenderType(): RenderType = when (this) {
    ScreenMode.Canvas -> RenderType.CANVAS
    ScreenMode.Agsl -> RenderType.AGSL
    ScreenMode.AgslCanvas -> RenderType.AGSL_CANVAS
}
