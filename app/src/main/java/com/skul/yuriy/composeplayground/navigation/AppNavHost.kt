package com.skul.yuriy.composeplayground.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skul.yuriy.composeplayground.feature.dynamicBackground.MetaballsRoute
import com.skul.yuriy.composeplayground.feature.parallax.ParallaxRoute
import com.skul.yuriy.composeplayground.feature.stickyHeader.StickyHeaderRoute
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
    }
}

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
    }
}