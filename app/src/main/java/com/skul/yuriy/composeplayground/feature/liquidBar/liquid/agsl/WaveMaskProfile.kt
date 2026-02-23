package com.skul.yuriy.composeplayground.feature.liquidBar.liquid.agsl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.Wave_1D

@Composable
internal fun rememberWaveProfileArgb(
    containerWidthPx: Int,
    containerHeightPx: Int,
    scale: Float,
    yGain: Float,
    sim: Wave_1D,
): IntArray {
    val width = containerWidthPx.coerceAtLeast(0)
    val height = containerHeightPx.coerceAtLeast(0)
    val profile = remember(width) { IntArray(width) }
    if (width <= 0 || height <= 0) return profile

    val safeScale = if (scale == 0f) 1f else scale
    val widthF = width.toFloat()
    val heightF = height.toFloat()
    for (x in 0 until width) {
        val xNorm = x.toFloat() / widthF
        val scaled = sim.sampleCurr(xNorm.coerceIn(0f, 1f)) * yGain
        val yPx = (0.5f - (scaled / safeScale)) * heightF
        val yNorm = (yPx / heightF).coerceIn(0f, 1f)
        val alpha = (yNorm * 255f).toInt().coerceIn(0, 255)
        profile[x] = (alpha shl 24) or 0x00FFFFFF
    }
    return profile
}

@Composable
internal fun rememberWaveProfileArgb16(
    width: Int,
    height: Int,
    scale: Float,
    yGain: Float,
    sim: Wave_1D,
): IntArray {
    val profile = remember(width) { IntArray(width.coerceAtLeast(0)) }
    if (width <= 0 || height <= 0) return profile

    val safeScale = if (scale == 0f) 1f else scale
    val widthF = width.toFloat()
    val heightF = height.toFloat()
    for (x in 0 until width) {
        val xNorm = x.toFloat() / widthF
        val scaled = sim.sampleCurr(xNorm.coerceIn(0f, 1f)) * yGain
        val yPx = (0.5f - (scaled / safeScale)) * heightF
        val yNorm = (yPx / heightF).coerceIn(0f, 1f)

        val y16 = (yNorm * 65535f).toInt().coerceIn(0, 65535)
        val hi = (y16 shr 8) and 0xFF
        val lo = y16 and 0xFF

        // Keep alpha=255 to avoid premultiplied RGB loss.
        profile[x] = (0xFF shl 24) or (hi shl 16) or (lo shl 8)
    }
    return profile
}
