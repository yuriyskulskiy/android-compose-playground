package com.skul.yuriy.composeplayground.starter

import android.content.Intent
import android.view.Surface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.sensorRotation.SensorRotationActivity
import com.skul.yuriy.composeplayground.navigation.navigateToAnimatedArk
import com.skul.yuriy.composeplayground.navigation.navigateToAnimatedBorderRect
import com.skul.yuriy.composeplayground.navigation.navigateToAnimatedCircularBtn
import com.skul.yuriy.composeplayground.navigation.navigateToAnimatedRectBtn
import com.skul.yuriy.composeplayground.navigation.navigateToAnimatedElevationEdge
import com.skul.yuriy.composeplayground.navigation.navigateToBottomEdgeShadowScreen
import com.skul.yuriy.composeplayground.navigation.navigateToCircularHaloBorder
import com.skul.yuriy.composeplayground.navigation.navigateToCustomAlphaBlur
import com.skul.yuriy.composeplayground.navigation.navigateToCustomAlphaBlurRadial
import com.skul.yuriy.composeplayground.navigation.navigateToFadingEdgesScreen
import com.skul.yuriy.composeplayground.navigation.navigateToGooeyScreen
import com.skul.yuriy.composeplayground.navigation.navigateToLiquidBar
import com.skul.yuriy.composeplayground.navigation.navigateToMetaballEdgeAdvanced
import com.skul.yuriy.composeplayground.navigation.navigateToMetaballEdgeHorizontalScroll
import com.skul.yuriy.composeplayground.navigation.navigateToMetaballEdges
import com.skul.yuriy.composeplayground.navigation.navigateToMetaballMath
import com.skul.yuriy.composeplayground.navigation.navigateToMetaballPrimer
import com.skul.yuriy.composeplayground.navigation.navigateToMetaballsScreen
import com.skul.yuriy.composeplayground.navigation.navigateToParallax
import com.skul.yuriy.composeplayground.navigation.navigateToShadowBox
import com.skul.yuriy.composeplayground.navigation.navigateToStickyHeaderStateTracker
import com.skul.yuriy.composeplayground.navigation.navigateToVectorIconWithShadow


@Composable
fun StarterRoute() {
    StarterScreen(modifier = Modifier.fillMaxSize())
}

@Composable
fun StarterScreen(
    modifier: Modifier = Modifier
) {

    NavigationContent(modifier = modifier)
}

@Composable
fun NavigationContent(modifier: Modifier) {
    val localNavBackStack = LocalNavBackStack.current
    val context = LocalContext.current
    val destinations = remember {
        listOf<StarterDestination>(
            ActivityStarterDestination(
                titleRes = R.string.sensor_rotation_demo,
                activityClass = SensorRotationActivity::class.java
            ),
            ComposeStarterDestination(R.string.animated_rect_button) {
                navigateToAnimatedRectBtn()
            },
            ComposeStarterDestination(R.string.animated_border_rect) {
                navigateToAnimatedBorderRect()
            },
            ComposeStarterDestination(R.string.liquid_bar) {
                navigateToLiquidBar()
            },
            ComposeStarterDestination(R.string.custom_alpha_blur_radial) {
                navigateToCustomAlphaBlurRadial()
            },
            ComposeStarterDestination(R.string.custom_alpha_blur) {
                navigateToCustomAlphaBlur()
            },
            ComposeStarterDestination(R.string.metaball_edge_horizontal_scroll) {
                navigateToMetaballEdgeHorizontalScroll()
            },
            ComposeStarterDestination(R.string.metaball_edge_advanced) {
                navigateToMetaballEdgeAdvanced()
            },
            ComposeStarterDestination(R.string.metaball_edge) {
                navigateToMetaballEdges()
            },
            ComposeStarterDestination(R.string.metaball_edge_text_title) {
                navigateToMetaballPrimer()
            },
            StarterYearDivider(label = "Year 2026"),
            ComposeStarterDestination(R.string.parallax_scroll_list) {
                navigateToParallax()
            },
            ComposeStarterDestination(R.string.metaballs_blur) {
                navigateToMetaballsScreen()
            },
            ComposeStarterDestination(R.string.topbar_animated_elevation) {
                navigateToAnimatedElevationEdge()
            },
            ComposeStarterDestination(R.string.fading_edges_screen) {
                navigateToFadingEdgesScreen()
            },
            ComposeStarterDestination(R.string.circular_halo_border) {
                navigateToCircularHaloBorder()
            },
            ComposeStarterDestination(R.string.animated_ark) {
                navigateToAnimatedArk()
            },
            ComposeStarterDestination(R.string.transparent_outline_shadow) {
                navigateToShadowBox()
            },
            ComposeStarterDestination(R.string.bottom_edge_shadow) {
                navigateToBottomEdgeShadowScreen()
            },
            ComposeStarterDestination(R.string.sticky_header_state_tracker) {
                navigateToStickyHeaderStateTracker()
            },
            ComposeStarterDestination(R.string.vector_drawable_shadow) {
                navigateToVectorIconWithShadow()
            },
            ComposeStarterDestination(R.string.animated_circular_button) {
                navigateToAnimatedCircularBtn()
            },
            ComposeStarterDestination(R.string.metaballs_classic_approach) {
                navigateToMetaballMath()
            }
        )
    }

    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        destinations.forEach { destination ->
            when (destination) {
                is StarterYearDivider -> YearDivider(label = destination.label)
                is ComposeStarterDestination -> NavigationItem(
                    text = stringResource(destination.titleRes),
                    onClick = {
                        destination.navigate.invoke(localNavBackStack)
                    }
                )
                is ActivityStarterDestination -> NavigationItem(
                    text = stringResource(destination.titleRes),
                    onClick = {
                        val intent = Intent(context, destination.activityClass)
                        if (destination.activityClass == SensorRotationActivity::class.java) {
                            val displayRotation = context.display?.rotation ?: Surface.ROTATION_0
                            intent.putExtra(
                                SensorRotationActivity.EXTRA_INITIAL_DISPLAY_ROTATION,
                                displayRotation
                            )
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}
