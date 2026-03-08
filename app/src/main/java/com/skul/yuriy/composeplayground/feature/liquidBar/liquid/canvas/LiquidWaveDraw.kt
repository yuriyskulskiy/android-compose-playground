package com.skul.yuriy.composeplayground.feature.liquidBar.liquid.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.InteractiveContentPosition
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.Wave1D
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
    sim: Wave1D,
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

    // Keep signature stable while hard-cut mode is active.
    plotWidth

    val pointsCount = max(2, sim.n.coerceIn(2, 700))
    val curvePoints = ArrayList<Offset>(pointsCount)
    for (i in 0 until pointsCount) {
        val xPx = (i.toFloat() / (pointsCount - 1).toFloat()) * wf
        // Wave model is bottom-up; Compose is top-down.
        val yScaled = sim.sampleCurrAt(i) * yGain
        val yCurvePx = (0.5f - (yScaled / scale)) * hf
        curvePoints += Offset(xPx, yCurvePx.coerceIn(0f, hf))
    }

    val solidPath = Path()
    if (isTopInteractive) {
        solidPath.moveTo(0f, 0f)
        solidPath.addSmoothedWaveCurve(curvePoints)
        solidPath.lineTo(wf, 0f)
        solidPath.close()
    } else {
        solidPath.moveTo(0f, hf)
        solidPath.addSmoothedWaveCurve(curvePoints)
        solidPath.lineTo(wf, hf)
        solidPath.close()
    }
    drawPath(path = solidPath, color = waveColor)
}

private fun Path.addSmoothedWaveCurve(points: List<Offset>) {
    if (points.isEmpty()) return
    if (points.size == 1) {
        lineTo(points[0].x, points[0].y)
        return
    }

    lineTo(points[0].x, points[0].y)
    if (points.size == 2) {
        lineTo(points[1].x, points[1].y)
        return
    }

    for (i in 1 until points.lastIndex) {
        val p = points[i]
        val next = points[i + 1]
        val midX = (p.x + next.x) * 0.5f
        val midY = (p.y + next.y) * 0.5f
        quadraticTo(p.x, p.y, midX, midY)
    }
    val last = points.last()
    lineTo(last.x, last.y)
}
