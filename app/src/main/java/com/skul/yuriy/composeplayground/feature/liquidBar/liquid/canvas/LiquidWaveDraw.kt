package com.skul.yuriy.composeplayground.feature.liquidBar.liquid.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.InteractiveContentPosition
import kotlin.math.max

internal fun DrawScope.drawLiquidWave(
    frameTick: Int,
    containerSize: IntSize,
    interactiveContentPosition: InteractiveContentPosition,
    bg: Color,
    waveColor: Color,
    plotWidth: Float,
    scale: Float,
    yGain: Float,
    sampleWave: (Float) -> Float,
) {
    if (frameTick < 0) return

    val isTopInteractive = interactiveContentPosition == InteractiveContentPosition.Top

    val w = containerSize.width
    val h = containerSize.height
    if (w <= 0 || h <= 0) return

    val wf = w.toFloat()
    val hf = h.toFloat()

    if (bg.alpha > 0f) {
        drawRect(bg)
    }

    // Convert plotWidth in "scaled space" into pixels.
    val bandPx = (plotWidth / scale) * hf

    // Work reduction on big widths.
    val stepPx = max(1, w / 700)

    var xPx = 0
    while (xPx < w) {
        val xNorm = xPx / wf

        // Sim value is in arbitrary units; scale it to match uv*=scale space.
        val yScaled = sampleWave(xNorm) * yGain

        // Wave model is bottom-up; Compose is top-down, so invert:
        // yPx = (0.5 - y/scale) * h
        val yCurvePx = (0.5f - (yScaled / scale)) * hf

        val yMid = yCurvePx.coerceIn(0f, hf)
        if (isTopInteractive) {
            val yBottom = (yMid + bandPx).coerceIn(0f, hf)

            // Soft edge for top-anchored wave (fade downwards).
            if (bandPx > 0.5f) {
                val slices = 6
                for (s in 0 until slices) {
                    val t = s.toFloat() / (slices - 1).toFloat()
                    val alpha = 1f - t
                    val y0 = yMid + t * (yBottom - yMid)
                    val y1 = yMid + (t + 1f / slices) * (yBottom - yMid)
                    drawRect(
                        color = waveColor.copy(alpha = alpha.coerceIn(0f, 1f)),
                        topLeft = Offset(xPx.toFloat(), y0),
                        size = Size(
                            stepPx.toFloat(),
                            (y1 - y0).coerceAtLeast(1f)
                        )
                    )
                }
            }

            // Solid fill above curve.
            drawRect(
                color = waveColor,
                topLeft = Offset(xPx.toFloat(), 0f),
                size = Size(
                    stepPx.toFloat(),
                    yMid.coerceAtLeast(0f)
                )
            )
        } else {
            val yTop = (yMid - bandPx).coerceIn(0f, hf)

            // Soft edge approximation (smoothstep band)
            if (bandPx > 0.5f) {
                val slices = 6
                for (s in 0 until slices) {
                    val t = s.toFloat() / (slices - 1).toFloat()
                    val alpha = 1f - t
                    val y0 = yTop + t * (yMid - yTop)
                    val y1 = yTop + (t + 1f / slices) * (yMid - yTop)
                    drawRect(
                        color = waveColor.copy(alpha = alpha.coerceIn(0f, 1f)),
                        topLeft = Offset(xPx.toFloat(), y0),
                        size = Size(
                            stepPx.toFloat(),
                            (y1 - y0).coerceAtLeast(1f)
                        )
                    )
                }
            }

            // Solid fill below curve (matches their "plot" feel)
            drawRect(
                color = waveColor,
                topLeft = Offset(xPx.toFloat(), yMid),
                size = Size(
                    stepPx.toFloat(),
                    (hf - yMid).coerceAtLeast(0f)
                )
            )
        }

        xPx += stepPx
    }
}
