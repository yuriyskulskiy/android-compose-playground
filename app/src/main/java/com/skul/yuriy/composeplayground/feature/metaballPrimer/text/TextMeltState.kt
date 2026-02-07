package com.skul.yuriy.composeplayground.feature.metaballPrimer.text

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Stable
class TextMeltState(
    private val scope: kotlinx.coroutines.CoroutineScope,
    private val animationDurationMs: Int,
    private val blurMaxDp: Dp,
) {
    private val days = listOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
    )

    private var dayIndex by mutableIntStateOf(0)
    private var isAnimating by mutableStateOf(false)
    private val blurAnim = Animatable(0.dp, Dp.VectorConverter)

    val currentDay: String
        get() = days[dayIndex]

    val blur: Dp
        get() = blurAnim.value

    fun next() {
        if (isAnimating) return
        scope.launch {
            isAnimating = true
            blurAnim.animateTo(blurMaxDp, animationSpec = tween(animationDurationMs))
            dayIndex = if (dayIndex == days.lastIndex) 0 else dayIndex + 1
            blurAnim.animateTo(0.dp, animationSpec = tween(animationDurationMs))
            isAnimating = false
        }
    }

    fun previous() {
        if (isAnimating) return
        scope.launch {
            isAnimating = true
            blurAnim.animateTo(blurMaxDp, animationSpec = tween(animationDurationMs))
            dayIndex = if (dayIndex == 0) days.lastIndex else dayIndex - 1
            blurAnim.animateTo(0.dp, animationSpec = tween(animationDurationMs))
            isAnimating = false
        }
    }
}

@Composable
fun rememberTextMeltState(
    blurMaxDp: Dp = 16.dp,
    animationDurationMs: Int = 3000,
): TextMeltState {
    val scope = rememberCoroutineScope()
    return remember(scope, blurMaxDp, animationDurationMs) {
        TextMeltState(
            scope = scope,
            animationDurationMs = animationDurationMs,
            blurMaxDp = blurMaxDp,
        )
    }
}
