package com.skul.yuriy.composeplayground.util

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ScreenBackground(
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(
        Color.Black.copy(alpha = 0.6f),
        blendMode = BlendMode.Darken
    ),
    showBackground: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    var containerHeightPx by remember { mutableIntStateOf(0) }
    var containerWidthPx by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current.density

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                containerHeightPx = size.height
                containerWidthPx = size.width
            }
    ) {
        if (showBackground && containerHeightPx > 0 && containerWidthPx > 0) {
            val scaledWidth = (containerWidthPx / density).toInt()
            val scaledHeight = (containerHeightPx / density).toInt()

            Image(
                colorFilter = colorFilter,
                contentScale = ContentScale.Crop,
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .size(
                        scaledWidth.dp,
                        scaledHeight.dp
                    )
            )
        }

        content()
    }
}
