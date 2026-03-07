package com.skul.yuriy.composeplayground.feature.liquidBar.liquid

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private fun touchPulseInfluence(center: Float, halfWidth: Float, x: Float): Float {
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
 *   curr[i] += touchPulseInfluence(clickX, width, x) * (1 - clickY) * amp
 */
internal class Wave1D(samples: Int) {
    private companion object {
        private const val RestEpsilon = 0.005f
        private const val RestStableFrames = 3
    }

    var n: Int = max(8, samples)
        private set

    private var curr = FloatArray(n) // data.x
    private var prev = FloatArray(n) // data.y
    private var next = FloatArray(n)

    var damping: Float = 0.99f
    var amp: Float = 5f
    var pulseWidthNorm: Float = 0.10f
    private var nearRestFrameCount: Int = RestStableFrames

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
        nearRestFrameCount = RestStableFrames
    }

    fun step(): Boolean {
        next[0] = 0f
        next[n - 1] = 0f

        val d = damping
        var maxAbsNext = 0f
        var maxDelta = 0f
        for (i in 1 until n - 1) {
            val value = (curr[i - 1] + curr[i + 1] - prev[i]) * d
            next[i] = value
            maxAbsNext = max(maxAbsNext, abs(value))
            maxDelta = max(maxDelta, abs(value - curr[i]))
        }

        val tmpPrev = prev
        prev = curr
        curr = next
        next = tmpPrev

        val isNearRest = maxAbsNext <= RestEpsilon && maxDelta <= RestEpsilon
        if (isNearRest) {
            nearRestFrameCount++
        } else {
            nearRestFrameCount = 0
        }
        return nearRestFrameCount < RestStableFrames
    }

    fun inject(clickXNorm: Float, clickYNormBottomUp: Float) {
        val cx = clickXNorm.coerceIn(0.1f, 0.9f)
        val cy = clickYNormBottomUp.coerceIn(0f, 1f)

        val strength = (1f - cy) * amp
        val w = pulseWidthNorm

        for (i in 0 until n) {
            val x = i.toFloat() / (n - 1).toFloat()
            val p = touchPulseInfluence(cx, w, x)
            if (p != 0f) curr[i] -= p * strength
        }
        nearRestFrameCount = 0
    }

    fun sampleCurr(xNorm: Float): Float {
        val x = xNorm.coerceIn(0f, 1f)
        val fx = x * (n - 1)
        val i0 = fx.toInt().coerceIn(0, n - 1)
        val i1 = min(i0 + 1, n - 1)
        val t = fx - i0
        return curr[i0] * (1f - t) + curr[i1] * t
    }

    fun sampleCurrAt(index: Int): Float {
        return curr[index.coerceIn(0, n - 1)]
    }
}
