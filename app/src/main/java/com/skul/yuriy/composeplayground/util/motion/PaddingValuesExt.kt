package com.skul.yuriy.composeplayground.util.motion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp

@Composable
internal fun PaddingValues.withAnimatedBottomInset(animatedBottom: Dp): PaddingValues {
    // Keep original insets but animate only the bottom for smooth bottom bar motion.
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateLeftPadding(layoutDirection),
        top = calculateTopPadding(),
        end = calculateRightPadding(layoutDirection),
        bottom = animatedBottom,
    )
}
