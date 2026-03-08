package com.skul.yuriy.composeplayground.feature.liquidBar.liquid

import android.os.Build

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.agsl.liquidWaveCanvasShader
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.agsl.rememberLiquidWaveRenderEffectOrNull
import com.skul.yuriy.composeplayground.feature.liquidBar.liquid.canvas.drawLiquidWave
import kotlinx.coroutines.isActive

enum class InteractiveContentPosition {
    Top,
    Bottom,
}

/**
 * Wave-based liquid container:
 * - Correct Y flip (wave model is bottom-up, Compose is top-down).
 * - Proper visual scaling via yGain to match uv*=1000 space.
 * - Continuous press+drag injection.
 * - Frame invalidation each step so it animates.
 *
 * @param contentAlignment Alignment for [content] inside the hit zone container.
 * @param modifier Root modifier for the liquid container.
 * @param samples Number of 1D simulation samples across width.
 * @param damping Wave damping factor per frame.
 * @param amp Touch injection amplitude for wave disturbance.
 * @param pulseWidthNorm Normalized width of injected pulse.
 * @param scale Vertical normalization scale used in wave projection.
 * @param plotWidth Visual width of soft wave edge in scaled units.
 * @param yGain Additional vertical gain for the sampled wave.
 * @param bg Background color for full liquid area.
 * @param waveColor Foreground liquid color.
 * @param dragThrottleMs Min time between touch injections while dragging.
 * @param renderType Render pipeline mode.
 * @param hitHeight Interactive zone height. If null, full container is interactive.
 * @param interactiveContentPosition Interactive zone position inside container.
 * @param content Bottom-zone content (typically navigation bar items).
 */
@Composable
fun LiquidBox(
    contentAlignment: Alignment = Alignment.TopStart,
    modifier: Modifier = Modifier,
//    samples: Int = 1024,
    samples: Int = 126,
    damping: Float = 0.97f,
    amp: Float = 0.5f,
    pulseWidthNorm: Float = 0.18f,
    scale: Float = 1000f,
    plotWidth: Float = 14f,
    yGain: Float = 18f,
    bg: Color = Color.Black,
    waveColor: Color = Color.White,
    dragThrottleMs: Long = 12L, // inject at most ~83 Hz while dragging
    renderType: RenderType = RenderType.CANVAS,
    hitHeight: Dp? = null,
    interactiveContentPosition: InteractiveContentPosition = InteractiveContentPosition.Bottom,
    content: @Composable () -> Unit
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var frameTick by remember { mutableIntStateOf(0) }
    var isSimulationRunning by remember { mutableStateOf(false) }

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
        isSimulationRunning = false
    }

    LaunchedEffect(isSimulationRunning) {
        if (!isSimulationRunning) return@LaunchedEffect
        while (isActive) {
            withFrameNanos {
                val stillActive = sim.step()
                frameTick++
                if (!stillActive) {
                    isSimulationRunning = false
                }
            }
        }
    }

    var hitZoneSize by remember { mutableStateOf(IntSize.Zero) }

    val renderModifier = when (renderType) {
        RenderType.CANVAS -> Modifier.drawBehind {
            drawLiquidWave(
                frameTick = frameTick,
                containerSize = containerSize,
                interactiveContentPosition = interactiveContentPosition,
                bg = bg,
                waveColor = waveColor,
                plotWidth = plotWidth,
                scale = scale,
                yGain = yGain,
                sim = sim,
            )
        }

        RenderType.AGSL -> {
            val effect = rememberLiquidWaveRenderEffectOrNull(
                frameTick = frameTick,
                containerSize = containerSize,
                interactiveContentPosition = interactiveContentPosition,
                waveColor = waveColor,
                plotWidth = plotWidth,
                scale = scale,
                yGain = yGain,
                sim = sim
            )
            if (effect == null) {
                Modifier
            } else {
                Modifier.graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    renderEffect = effect
                }
            }
        }

        RenderType.AGSL_CANVAS -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                Modifier.drawBehind {
                    drawLiquidWave(
                        frameTick = frameTick,
                        containerSize = containerSize,
                        interactiveContentPosition = interactiveContentPosition,
                        bg = bg,
                        waveColor = waveColor,
                        plotWidth = plotWidth,
                        scale = scale,
                        yGain = yGain,
                        sim = sim,
                    )
                }
            } else {
                Modifier.liquidWaveCanvasShader(
                    frameTick = frameTick,
                    containerSize = containerSize,
                    interactiveContentPosition = interactiveContentPosition,
                    waveColor = waveColor,
                    plotWidth = plotWidth,
                    scale = scale,
                    yGain = yGain,
                    sim = sim,
                    bg = bg
                )
            }
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { containerSize = it }
            .then(renderModifier)
    ) {
        val hitZoneModifier = if (hitHeight != null) {
            when (interactiveContentPosition) {
                InteractiveContentPosition.Top -> Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(hitHeight)

                InteractiveContentPosition.Bottom -> Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(hitHeight)
            }
        } else {
            Modifier.fillMaxSize()
        }

        Box(
            contentAlignment = contentAlignment,
            modifier = hitZoneModifier
                .onSizeChanged { hitZoneSize = it }
                .pointerInput(dragThrottleMs, interactiveContentPosition) {
                    awaitPointerEventScope {
                        listenWaveDragEvents(
                            dragThrottleMs = dragThrottleMs,
                            containerSizeProvider = { containerSize },
                            hitZoneSizeProvider = { hitZoneSize },
                            interactiveContentPosition = interactiveContentPosition,
                            sim = sim,
                            onWaveInjected = {
                                if (!isSimulationRunning) {
                                    isSimulationRunning = true
                                    frameTick++
                                }
                            }
                        )
                    }
                }
        ) {
            content()
        }
    }
}
