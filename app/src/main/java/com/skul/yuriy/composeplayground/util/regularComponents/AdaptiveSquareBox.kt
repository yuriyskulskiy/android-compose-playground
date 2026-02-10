package com.skul.yuriy.composeplayground.util.regularComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
fun AdaptiveSquareBoxBasedOnOrientation(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val containerSize = LocalWindowInfo.current.containerSize
    val isPortrait = containerSize.height >= containerSize.width

    val boxModifier = if (isPortrait) {
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // In portrait, use width as the square base.
    } else {
        Modifier
            .fillMaxHeight()
            .aspectRatio(1f) // In landscape, use height as the square base.
    }

    Box(
        modifier = modifier.then(boxModifier)
    ) {
        content()
    }
}
