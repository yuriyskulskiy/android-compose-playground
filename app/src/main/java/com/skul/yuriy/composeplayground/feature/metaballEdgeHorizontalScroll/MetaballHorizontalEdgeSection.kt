package com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RenderEffect as ComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.dynamic.alphaGaussianBlurByWidth
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold50PercentEffect

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MetaballHorizontalEdgeSection(
    modifier: Modifier = Modifier,
    sectionHeight: Dp = 116.dp,
    effectMagnitude: Dp = 56.dp,
    blurRadius: Dp = 30.dp,
    contentHorizontalPadding: Dp = 16.dp,
    itemSpacing: Dp = 16.dp,
    alphaThresholdEffect: ComposeRenderEffect? = alphaThreshold50PercentEffect,
    edgeAlpha: Float = 0.5f,
    content: @Composable RowScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val isAtStart by remember(scrollState) {
        derivedStateOf { scrollState.value == 0 }
    }
    val isAtEnd by remember(scrollState) {
        derivedStateOf { scrollState.value == scrollState.maxValue }
    }
    val leftBlurRadius by animateDpAsState(
        targetValue = if (isAtStart) 0.dp else blurRadius,
        animationSpec = tween(durationMillis = 600),
        label = "left-metaball-edge-blur"
    )
    val rightBlurRadius by animateDpAsState(
        targetValue = if (isAtEnd) 0.dp else blurRadius,
        animationSpec = tween(durationMillis = 600),
        label = "right-metaball-edge-blur"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(sectionHeight)
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
                renderEffect = alphaThresholdEffect
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .alphaGaussianBlurByWidth(
                    leftRadius = leftBlurRadius,
                    rightRadius = rightBlurRadius,
                    leftEdgeWidth = effectMagnitude,
                    rightEdgeWidth = effectMagnitude,
                )
                .horizontalScroll(scrollState)
                .padding(horizontal = contentHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            content = content
        )

        MetaballHorizontalGradientEdges(
            sectionHeight = sectionHeight,
            effectMagnitude = effectMagnitude,
            edgeAlpha = edgeAlpha,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun BoxScope.MetaballHorizontalGradientEdges(
    sectionHeight: Dp,
    effectMagnitude: Dp,
    edgeAlpha: Float,
) {
    Box(
        modifier = Modifier
            .align(Alignment.CenterStart)
            .fillMaxHeight()
            .size(width = effectMagnitude, height = sectionHeight)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = edgeAlpha),
                        Color.Black.copy(alpha = 0f),
                    )
                )
            )
    )

    Box(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .size(width = effectMagnitude, height = sectionHeight)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0f),
                        Color.Black.copy(alpha = edgeAlpha),
                    )
                )
            )
    )
}
