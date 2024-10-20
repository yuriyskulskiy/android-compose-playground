package com.skul.yuriy.composeplayground.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skul.yuriy.composeplayground.draft.AnimatedCircularBtnScreen
import com.skul.yuriy.composeplayground.feature.animatedBorder.CircularHaloShadowScreen
import com.skul.yuriy.composeplayground.feature.metaball.MetaballsRoute
import com.skul.yuriy.composeplayground.feature.parallax.ParallaxRoute
import com.skul.yuriy.composeplayground.feature.scrollEdge.animatedElevation.AnimatedElevationRoute
import com.skul.yuriy.composeplayground.feature.scrollEdge.fadingEdge.FadingEdgesRoute
import com.skul.yuriy.composeplayground.feature.shadowBox.OutlineShadowBoxRoute
import com.skul.yuriy.composeplayground.feature.stickyHeader.StickyHeaderRoute
import com.skul.yuriy.composeplayground.feature.rotationArk.AnimatedArkScreen
import com.skul.yuriy.composeplayground.starter.StarterRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToParallax() {
    navigate(Screens.Route.Parallax)
}

fun NavController.navigateToStickyHeaderStateTracker() {
    navigate(Screens.Route.StickyHeaderStateTracker)
}

fun NavController.navigateToMetaballsScreen() {
    navigate(Screens.Route.MetaballScreen)
}

fun NavController.navigateToAnimatedElevationEdge() {
    navigate(Screens.Route.AnimatedElevationEdge)
}

fun NavController.navigateToFadingEdgesScreen() {
    navigate(Screens.Route.FadingEdgesScreen)
}

fun NavController.navigateToCircularHaloBorder() {
    navigate(Screens.Route.CircularHaloShadow)
}

fun NavController.navigateToAnimatedBorder() {
    navigate(Screens.Route.AnimatedBorder)
}

fun NavController.navigateToShadowBox() {
    navigate(Screens.Route.TransparentShadowBox)
}

fun NavController.navigateToAnimatedArk() {
    navigate(Screens.Route.AnimatedArk)
}

@Serializable
sealed class Screens {

    @Serializable
    object Route {
        @Serializable
        object Starter

        @Serializable
        object Parallax

        @Serializable
        object StickyHeaderStateTracker

        @Serializable
        object MetaballScreen

        @Serializable
        object CircularAnimatedButton

        @Serializable
        object AnimatedElevationEdge

        @Serializable
        object FadingEdgesScreen

        @Serializable
        object AnimatedBorder

        @Serializable
        object CircularHaloShadow

        @Serializable
        object AnimatedArk

        @Serializable
        object TransparentShadowBox
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
internal fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Route.Starter,
        modifier = modifier
    ) {

        composable<Screens.Route.Starter> {
            StarterRoute()
        }
        composable<Screens.Route.Parallax> {
            ParallaxRoute()
        }
        composable<Screens.Route.StickyHeaderStateTracker> {
            StickyHeaderRoute()
        }

        composable<Screens.Route.MetaballScreen> {
            MetaballsRoute()
        }

        composable<Screens.Route.AnimatedElevationEdge> {
            AnimatedElevationRoute()
        }

        composable<Screens.Route.FadingEdgesScreen> {
            FadingEdgesRoute()
        }
        composable<Screens.Route.CircularHaloShadow> {
            CircularHaloShadowScreen()
        }

        composable<Screens.Route.AnimatedBorder> {
            AnimatedCircularBtnScreen()
        }

        composable<Screens.Route.TransparentShadowBox> {
            OutlineShadowBoxRoute()
        }

        composable<Screens.Route.AnimatedArk> {
            AnimatedArkScreen()
        }
    }
}