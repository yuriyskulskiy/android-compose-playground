package com.skul.yuriy.composeplayground.feature.liquidBar

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

 internal fun Modifier.invertByDifferenceBlend(): Modifier = graphicsLayer {
     compositingStrategy = CompositingStrategy.Offscreen
     blendMode = BlendMode.Difference
 }

//internal fun Modifier.invertByDifferenceBlend(): Modifier = this
