package com.skul.yuriy.composeplayground.feature.scrollEdge.animatedElevation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.Dp


/**
 * Extension function to animate the elevation change of a `Dp` value.
 *
 * This function returns a `State` object that animates the provided `Dp` value over a specified
 * duration, allowing for a smooth transition of elevation changes (such as toolbar shadow effects).
 *
 * The animation duration can be customized via the `durationMillis` parameter.
 *
 * @param durationMillis The duration of the animation in milliseconds. The default is 300ms.
 * @return A `State<Dp>` that represents the animated `Dp` value.
 */
@Composable
fun Dp.animateElevation(durationMillis: Int = 300): State<Dp> {
    return animateDpAsState(
        targetValue = this,
        animationSpec = tween(durationMillis = durationMillis),
        label = "toolbar shadow animation"
    )
}

/**
 * Helper function to check if the `LazyListState` is scrolled to the top.
 *
 * This function returns `true` if the first visible item is at index 0 and the scroll offset is 0,
 * indicating that the lazy list is scrolled to the very top.
 *
 * @return `true` if the list is at the top, `false` otherwise.
 */
fun LazyListState.isLazyListAtTop() =
    firstVisibleItemIndex == 0 &&
            firstVisibleItemScrollOffset == 0

/**
 * Helper function to check if the `ScrollState` (used in a regular scrollable `Column`)
 * is scrolled to the top.
 *
 * This function returns `true` if the current scroll position is at the top (scroll value is 0),
 * indicating that the column is scrolled to the very top.
 *
 * @return `true` if the column is at the top, `false` otherwise.
 */
fun ScrollState.isColumnAtTop() = value == 0