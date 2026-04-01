package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing.AlphaRotationAngleSmoother
import com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing.AnimatedRotationAngleSmoother
import com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing.IRotationAngleSmoother
import com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing.SpringLockRotationAngleSmoother
import kotlinx.coroutines.CoroutineScope

internal enum class SmoothingUiState(
    val label: String
) {
    SmoothAlpha(label = "smoothAlpha") {
        override fun createSmoother(scope: CoroutineScope): IRotationAngleSmoother =
            AlphaRotationAngleSmoother()
    },
    AnimateTo(label = "animateTo") {
        override fun createSmoother(scope: CoroutineScope): IRotationAngleSmoother =
            AnimatedRotationAngleSmoother(scope)
    },
    SpringLock(label = "springLock") {
        override fun createSmoother(scope: CoroutineScope): IRotationAngleSmoother =
            SpringLockRotationAngleSmoother(scope)
    };

    abstract fun createSmoother(scope: CoroutineScope): IRotationAngleSmoother

    fun next(): SmoothingUiState = entries[(ordinal + 1) % entries.size]
}
