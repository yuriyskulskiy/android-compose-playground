package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing.IRotationAngleSmoother
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.AccelerometerRotationAngleSource
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.OrientationEventRotationAngleSource
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.RotationAngleSourceType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
internal fun rememberRotationAngle(
//    sourceType: RotationAngleSourceType = ACCELEROMETER_ROTATION_ANGLE_SOURCE,
    smoothingType: SmoothingUiState = DEFAULT_SMOOTHING_TYPE,
    sourceType: RotationAngleSourceType = ORIENTATION_EVENT_LISTENER_ROTATION_ANGLE_SOURCE
): Float {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val angleSource = remember(context, sourceType) {
        when (sourceType) {
            RotationAngleSourceType.Accelerometer -> AccelerometerRotationAngleSource(context)
            RotationAngleSourceType.OrientationEventListener -> {
                OrientationEventRotationAngleSource(context)
            }
        }
    }
    val angleSmoother: IRotationAngleSmoother = remember(smoothingType, scope) {
        smoothingType.createSmoother(scope)
    }
    val currentOnAngleChanged by rememberUpdatedState<(Float) -> Unit> { angleSmoother.onRawAngle(it) }

    DisposableEffect(angleSource, angleSmoother) {
        val angleEvents = callbackFlow {
            angleSource.start { angle ->
                trySend(angle)
            }
            awaitClose {
                angleSource.stop()
            }
        }
        val collectJob = scope.launch {
            angleEvents
                .conflate()
                .distinctUntilChanged()
                .collect { angle ->
                    currentOnAngleChanged(angle)
                }
        }

        onDispose {
            collectJob.cancel()
            angleSmoother.reset()
        }
    }

    return angleSmoother.angle
}

private val ACCELEROMETER_ROTATION_ANGLE_SOURCE = RotationAngleSourceType.Accelerometer
private val ORIENTATION_EVENT_LISTENER_ROTATION_ANGLE_SOURCE = RotationAngleSourceType.OrientationEventListener
private val DEFAULT_SMOOTHING_TYPE = SmoothingUiState.SmoothAlpha
