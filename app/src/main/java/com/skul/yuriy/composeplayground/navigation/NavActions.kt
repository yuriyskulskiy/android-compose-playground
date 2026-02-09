package com.skul.yuriy.composeplayground.navigation

import androidx.annotation.MainThread
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skul.yuriy.composeplayground.util.navigation.popFrameLocked
import com.skul.yuriy.composeplayground.util.navigation.popInProgressLocked

// Base navigation primitives
private fun NavBackStack<NavKey>.navigate(screen: Screens) {
    add(screen)
}



//Unsafe!!!
 fun NavBackStack<NavKey>.pop(): Boolean {
    return removeLastOrNull() != null
}

/**
 * Delegate "navigate up" for Nav3.
 *
 * Current choice: uses [popInProgressLocked] to prevent nested/duplicated synchronous pops
 * (e.g. two callbacks firing in the same call stack).
 *
 * You can later change the implementation to [popFrameLocked] (frame-based guard) or any other
 * policy without touching call sites.
 *
 * @return true if something was popped; false if already at root or blocked by the guard.
 */
@MainThread
fun NavBackStack<NavKey>.navigateUp(): Boolean {
    return popInProgressLocked()
}

fun NavBackStack<NavKey>.navigateToParallax() =
    navigate(Screens.Parallax)

fun NavBackStack<NavKey>.navigateToStickyHeaderStateTracker() =
    navigate(Screens.StickyHeaderStateTracker)

fun NavBackStack<NavKey>.navigateToMetaballsScreen() =
    navigate(Screens.MetaballScreen)

fun NavBackStack<NavKey>.navigateToAnimatedElevationEdge() =
    navigate(Screens.AnimatedElevationEdge)

fun NavBackStack<NavKey>.navigateToFadingEdgesScreen() =
    navigate(Screens.FadingEdgesScreen)

fun NavBackStack<NavKey>.navigateToVectorIconWithShadow() =
    navigate(Screens.VectorDrawableShadow)

fun NavBackStack<NavKey>.navigateToCircularHaloBorder() =
    navigate(Screens.CircularHaloShadow)

fun NavBackStack<NavKey>.navigateToShadowBox() =
    navigate(Screens.TransparentShadowBox)

fun NavBackStack<NavKey>.navigateToAnimatedArk() =
    navigate(Screens.AnimatedArk)

fun NavBackStack<NavKey>.navigateToBottomEdgeShadowScreen() =
    navigate(Screens.BottomEdgeShadow)

fun NavBackStack<NavKey>.navigateToAnimatedCircularBtn() =
    navigate(Screens.AnimateCircularButton)

fun NavBackStack<NavKey>.navigateToGooeyScreen() =
    navigate(Screens.GooeyEffect)

fun NavBackStack<NavKey>.navigateToMetaballMath() =
    navigate(Screens.MetaballClassicMath)

fun NavBackStack<NavKey>.navigateToMetaballEdges() =
    navigate(Screens.MetaballTextEdges)

fun NavBackStack<NavKey>.navigateToCustomBlur() =
    navigate(Screens.CustomBlur)

fun NavBackStack<NavKey>.navigateToMetaballPrimer() =
    navigate(Screens.MetaballBasicTextAndEdge)

fun NavBackStack<NavKey>.navigateToTextMetabalConcept() =
    navigate(Screens.TextMetaballConcept)
