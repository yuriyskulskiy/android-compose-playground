package com.skul.yuriy.composeplayground.feature.liquidBar.prototype

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.draw.drawWithCache


import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import com.skul.yuriy.composeplayground.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min



private fun cubicPulse(center: Float, halfWidth: Float, x: Float): Float {
    var d = abs(x - center)
    if (d > halfWidth) return 0f
    d /= halfWidth
    return 1f - d * d * (3f - 2f * d)
}

/**
 * 1D wave buffer:
 * - curr[i] == data.x (R)
 * - prev[i] == data.y (G)
 *
 * Step:
 *   next = left + right - prev
 *   next *= damping
 *
 * Mouse/Touch injection (continuous):
 *   curr[i] += cubicPulse(clickX, width, x) * (1 - clickY) * amp
 */
private class Wave1D(samples: Int) {
    var n: Int = max(8, samples)
        private set

    private var curr = FloatArray(n) // data.x
    private var prev = FloatArray(n) // data.y
    private var next = FloatArray(n)

    var damping: Float = 0.99f
    var amp: Float = 5f
    var pulseWidthNorm: Float = 0.10f

    fun resize(samples: Int) {
        val newN = max(8, samples)
        if (newN == n) return

        val newCurr = FloatArray(newN)
        val newPrev = FloatArray(newN)
        val newNext = FloatArray(newN)

        val copyN = min(n, newN)
        for (i in 0 until copyN) {
            newCurr[i] = curr[i]
            newPrev[i] = prev[i]
            newNext[i] = next[i]
        }

        n = newN
        curr = newCurr
        prev = newPrev
        next = newNext
    }

    fun step() {
        next[0] = 0f
        next[n - 1] = 0f

        val d = damping
        for (i in 1 until n - 1) {
            val value = (curr[i - 1] + curr[i + 1] - prev[i]) * d
            next[i] = value
        }

        val tmpPrev = prev
        prev = curr
        curr = next
        next = tmpPrev
    }

    fun inject(clickXNorm: Float, clickYNormBottomUp: Float) {
        val cx = clickXNorm.coerceIn(0.1f, 0.9f)
        val cy = clickYNormBottomUp.coerceIn(0f, 1f)

        val strength = (1f - cy) * amp
        val w = pulseWidthNorm

        for (i in 0 until n) {
            val x = i.toFloat() / (n - 1).toFloat()
            val p = cubicPulse(cx, w, x)
            if (p != 0f) curr[i] -= p * strength
        }
    }

    fun sampleCurr(xNorm: Float): Float {
        val x = xNorm.coerceIn(0f, 1f)
        val fx = x * (n - 1)
        val i0 = fx.toInt().coerceIn(0, n - 1)
        val i1 = min(i0 + 1, n - 1)
        val t = fx - i0
        return curr[i0] * (1f - t) + curr[i1] * t
    }
}

/**
 * Wave-based 1D renderer:
 * - Correct Y flip (wave model is bottom-up, Compose is top-down).
 * - Proper visual scaling via yGain to match uv*=1000 space.
 * - Continuous press+drag injection.
 * - Frame invalidation each step so it animates.
 */
@Composable
fun Wave1DLike(
    modifier: Modifier = Modifier,
    samples: Int = 2048,
    damping: Float = 0.99f,
    amp: Float = 12f,
    pulseWidthNorm: Float = 0.16f,
    scale: Float = 1000f,
    plotWidth: Float = 14f,
    yGain: Float = 160f,
    bg: Color = Color.Black,
    waveColor: Color = Color.White,
    dragThrottleMs: Long = 12L, // inject at most ~83 Hz while dragging
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var frameTick by remember { mutableIntStateOf(0) }

    val sim = remember {
        Wave1D(samples).apply {
            this.damping = damping
            this.amp = amp
            this.pulseWidthNorm = pulseWidthNorm
        }
    }

    LaunchedEffect(samples, damping, amp, pulseWidthNorm) {
        sim.resize(samples)
        sim.damping = damping
        sim.amp = amp
        sim.pulseWidthNorm = pulseWidthNorm
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                sim.step()
                frameTick++
            }
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(dragThrottleMs) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)

                    var lastInjectMs = 0L

                    fun injectAt(pos: Offset, nowMs: Long) {
                        if (size.width <= 0 || size.height <= 0) return

                        val cx = (pos.x / size.width.toFloat()).coerceIn(0f, 1f)
                        val cyTopDown = (pos.y / size.height.toFloat()).coerceIn(0f, 1f)
                        val cyBottomUp = 1f - cyTopDown

                        sim.inject(cx, cyBottomUp)
                        lastInjectMs = nowMs
                    }

                    val startMs = (down.uptimeMillis)
                    injectAt(down.position, startMs)

                    drag(down.id) { change ->
                        val nowMs = change.uptimeMillis
                        if (nowMs - lastInjectMs >= dragThrottleMs) {
                            injectAt(change.position, nowMs)
                        }
                        change.consume()
                    }
                }
            }
            .drawWithCache {
                onDrawBehind {
                    val tick = frameTick

                    val w = size.width
                    val h = size.height
                    if (w <= 0 || h <= 0) return@onDrawBehind

                    val wf = w.toFloat()
                    val hf = h.toFloat()

                    drawRect(bg)

                    // Convert plotWidth in "scaled space" into pixels.
                    val bandPx = (plotWidth / scale) * hf

                    // Work reduction on big widths.
                    val stepPx = max(1, w / 700)

                    var xPx = 0
                    while (xPx < w) {
                        val xNorm = xPx / wf

                        // Sim value is in arbitrary units; scale it to match uv*=scale space.
                        val Yscaled = sim.sampleCurr(xNorm) * yGain

                        // Wave model is bottom-up; Compose is top-down, so invert:
                        // yPx = (0.5 - y/scale) * h
                        val yCurvePx = (0.5f - (Yscaled / scale)) * hf

                        val yMid = yCurvePx.coerceIn(0f, hf)
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

                        xPx += stepPx
                    }
                }
            }
    ) {
        Text(
            text = stringResource(R.string.very_long_mock_text).trimIndent(),
//            color = Color.Red,
//            color = Color(0xFFE53935),
//            color = Color(0xFF2196F3), //Blue 500
            color = Color(0xFF3F51B5) // Indigo 500,
//            modifier = Modifier.graphicsLayer {
//                blendMode = BlendMode.Difference
//            }
        )
    }
}
