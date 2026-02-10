package com.skul.yuriy.composeplayground

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.skul.yuriy.composeplayground.feature.animatedBorder.CircularHaloShadowScreen
import com.skul.yuriy.composeplayground.feature.animatedCircularButton.AnimatedCircularBtnScreen
import com.skul.yuriy.composeplayground.feature.bottomEdge.BottomEdgeShadowScreen
import com.skul.yuriy.composeplayground.feature.customBlur.CustomBlurScreen
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.GooeyBasicScreen
import com.skul.yuriy.composeplayground.feature.metaballEdgeText.MetaballEdgeTextScreen
import com.skul.yuriy.composeplayground.feature.metaballEdgeText.text.concept.TextMetaballConceptScreen
import com.skul.yuriy.composeplayground.feature.metaballBlur.MetaballsScreen
import com.skul.yuriy.composeplayground.feature.metaballClassic.MetaballClassicScreen
import com.skul.yuriy.composeplayground.feature.metaballTextEdge.MetaballTextEdgeScreen
import com.skul.yuriy.composeplayground.feature.parallax.ParallaxRoute
import com.skul.yuriy.composeplayground.feature.rotationArk.AnimatedArkScreen
import com.skul.yuriy.composeplayground.feature.scrollEdge.animatedElevation.AnimatedElevationRoute
import com.skul.yuriy.composeplayground.feature.scrollEdge.fadingEdge.FadingEdgesRoute
import com.skul.yuriy.composeplayground.feature.shadowBox.OutlineShadowBoxRoute
import com.skul.yuriy.composeplayground.feature.stickyHeader.StickyHeaderRoute
import com.skul.yuriy.composeplayground.feature.vectorIconShadow.VectorDrawableShadowScreen
import com.skul.yuriy.composeplayground.navigation.Screens
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.starter.StarterRoute

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun AppRoot(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
) {

    BackHandler(enabled = backStack.size > 1) {
        backStack.navigateUp()
    }

    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this@SharedTransitionLayout) {
            NavDisplay(
                backStack = backStack,
                // Adds SaveableState support per destination
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    // Provides a destination-scoped ViewModelStoreOwner
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<Screens.Starter> { StarterRoute() }
                    entry<Screens.Parallax> { ParallaxRoute() }
                    entry<Screens.StickyHeaderStateTracker> { StickyHeaderRoute() }
                    entry<Screens.MetaballScreen> { MetaballsScreen() }
                    entry<Screens.AnimatedElevationEdge> { AnimatedElevationRoute() }
                    entry<Screens.FadingEdgesScreen> { FadingEdgesRoute() }
                    entry<Screens.CircularHaloShadow> { CircularHaloShadowScreen() }
                    entry<Screens.VectorDrawableShadow> { VectorDrawableShadowScreen() }
                    entry<Screens.TransparentShadowBox> { OutlineShadowBoxRoute() }
                    entry<Screens.AnimatedArk> { AnimatedArkScreen() }
                    entry<Screens.BottomEdgeShadow> { BottomEdgeShadowScreen() }
                    entry<Screens.AnimateCircularButton> { AnimatedCircularBtnScreen() }
                    entry<Screens.GooeyEffect> { GooeyBasicScreen() }
                    entry<Screens.MetaballClassicMath> { MetaballClassicScreen() }
                    entry<Screens.MetaballTextEdges> { MetaballTextEdgeScreen() }
                    entry<Screens.CustomBlur> { CustomBlurScreen() }
                    entry<Screens.MetaballBasicTextAndEdge> { MetaballEdgeTextScreen() }
                    entry<Screens.TextMetaballConcept> { TextMetaballConceptScreen() }
                },
                modifier = modifier
            )
        }
    }
}
