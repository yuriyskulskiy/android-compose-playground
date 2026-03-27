package com.skul.yuriy.composeplayground.feature.animatedRectButton.snake

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

private const val SnakeLoopAnimationDurationMs = 3500

private fun normalizeSnakeProgress(progress: Float): Float = ((progress % 1f) + 1f) % 1f

@Stable
class RectSnakeState internal constructor(
    initialProgress: Float = 0f
) {
    var progress by mutableFloatStateOf(normalizeSnakeProgress(initialProgress))
        internal set

    internal var isEnabled by mutableStateOf(true)
    internal var isLifecycleRunning by mutableStateOf(true)

    val isRunning: Boolean
        get() = isEnabled && isLifecycleRunning

    companion object {
        val Saver: Saver<RectSnakeState, Float> = Saver(
            save = { it.progress },
            restore = { RectSnakeState(initialProgress = it) }
        )
    }
}

@Composable
fun rememberRectSnakeState(
    enabled: Boolean = true
): RectSnakeState {
    val state = rememberSaveable(saver = RectSnakeState.Saver) {
        RectSnakeState()
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    state.isEnabled = enabled

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP -> {
                    state.isLifecycleRunning = false
                }

                Lifecycle.Event.ON_RESUME -> {
                    state.isLifecycleRunning = true
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.isRunning) {
        if (!state.isRunning) return@LaunchedEffect

        val startProgress = state.progress
        val startFrameNanos = withFrameNanos { it }

        while (true) {
            val frameNanos = withFrameNanos { it }
            val elapsedMs = (frameNanos - startFrameNanos) / 1_000_000f
            val loopProgress = elapsedMs / SnakeLoopAnimationDurationMs
            state.progress = normalizeSnakeProgress(startProgress + loopProgress)
        }
    }

    return state
}
