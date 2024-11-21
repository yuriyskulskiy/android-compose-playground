package com.skul.yuriy.composeplayground.util.regularComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun AdaptiveSquareBoxBasedOnOrientation(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.screenHeightDp > configuration.screenWidthDp

    val boxModifier = if (isPortrait) {
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Используем ширину как базу в портретной ориентации
    } else {
        Modifier
            .fillMaxHeight()
            .aspectRatio(1f) // Используем высоту как базу в ландшафтной ориентации
    }

    Box(
        modifier = modifier.then(boxModifier)
    ) {
        content()
    }
}