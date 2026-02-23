package com.skul.yuriy.composeplayground.feature.liquidBar.liquid.agsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RenderEffect as ComposeRenderEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.InteractiveContentPosition
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.Wave_1D
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.resolveHitZoneTopPx

@Composable
internal fun rememberWaveMaskEffectOrNull(
    useAgsl: Boolean,
    frameTick: Int,
    containerSize: IntSize,
    hitZoneSize: IntSize,
    hitHeight: Dp?,
    interactiveContentPosition: InteractiveContentPosition,
    scale: Float,
    yGain: Float,
    sim: Wave_1D,
): ComposeRenderEffect? {
    if (!useAgsl) return null

    val waveProfileArgb = rememberWaveProfileArgb(
        containerWidthPx = containerSize.width,
        containerHeightPx = containerSize.height,
        scale = scale,
        yGain = yGain,
        sim = sim
    )
    val hitZoneTopPx = resolveHitZoneTopPx(
        containerHeightPx = containerSize.height,
        hitZoneHeightPx = hitZoneSize.height,
        hitHeight = hitHeight,
        interactiveContentPosition = interactiveContentPosition
    )

    return rememberLiquidAlphaMaskEffect(
        frameTick = frameTick,
        containerWidthPx = containerSize.width,
        containerHeightPx = containerSize.height,
        hitZoneHeightPx = if (hitHeight != null) hitZoneSize.height else containerSize.height,
        hitZoneTopPx = hitZoneTopPx,
        waveProfileArgb = waveProfileArgb
    )
}
