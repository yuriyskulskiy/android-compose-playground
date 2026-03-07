package com.skul.yuriy.composeplayground.feature.liquidBar.liquid.agsl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.Wave1D

/**
 * Builds a 1-row ARGB profile where each pixel stores the wave Y for one X column.
 *
 * Encoding:
 * - `yNorm` (normalized wave height in `[0..1]`) is quantized to 16-bit: `y16 = round(yNorm * 65535)`.
 * - High byte is stored in `R`, low byte in `G`.
 * - `A` is forced to `255` to avoid premultiplied alpha altering RGB payload.
 * - `B` is unused.
 *
 * Decoding in AGSL (see `LiquidWaveRenderEffect`):
 * - `hi = round(R * 255)`, `lo = round(G * 255)`.
 * - `y16 = hi * 256 + lo`.
 * - `yNorm = y16 / 65535`.
 */
@Composable
internal fun rememberWaveProfileArgb16(
    profileWidth: Int,
    height: Int,
    scale: Float,
    yGain: Float,
    sim: Wave1D,
): IntArray {
    val profile = remember(profileWidth) { IntArray(profileWidth.coerceAtLeast(0)) }
    if (profileWidth <= 0 || height <= 0) return profile

    val safeScale = if (scale == 0f) 1f else scale
    val heightF = height.toFloat()
    for (x in 0 until profileWidth) {
        val scaled = sim.sampleCurrAt(x) * yGain
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
