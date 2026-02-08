package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Applies a RenderEffect on a new offscreen layer. Useful for blur/alpha
 * effects that should be applied to the composited subtree.
 */
fun Modifier.applyRenderEffect(
    effect: RenderEffect,
    clip: Boolean = true,
    compositingStrategy: CompositingStrategy = CompositingStrategy.Offscreen,
): Modifier = graphicsLayer {
    this.compositingStrategy = compositingStrategy
    this.clip = clip
    renderEffect = effect
}
