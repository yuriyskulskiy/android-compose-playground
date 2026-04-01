package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.AccelerometerRotationAngleSource
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.OrientationEventRotationAngleSource
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.RotationAngleSourceType

@Composable
internal fun rememberRotationAngle(
    sourceType: RotationAngleSourceType = DEFAULT_ROTATION_ANGLE_SOURCE
//    sourceType: RotationAngleSourceType = ALTERNATIVE_ROTATION_ANGLE_SOURCE
): Float {
    val context = LocalContext.current
    val angleSource = remember(context, sourceType) {
        when (sourceType) {
            RotationAngleSourceType.Accelerometer -> AccelerometerRotationAngleSource(context)
            RotationAngleSourceType.OrientationEventListener -> {
                OrientationEventRotationAngleSource(context)
            }
        }
    }
    var tiltAngle by remember { mutableFloatStateOf(0f) }
    val currentOnAngleChanged by rememberUpdatedState<(Float) -> Unit> { tiltAngle = it }

    DisposableEffect(angleSource) {
        angleSource.start { angle ->
            currentOnAngleChanged(angle)
        }
        onDispose(angleSource::stop)
    }

    return tiltAngle
}

private val DEFAULT_ROTATION_ANGLE_SOURCE = RotationAngleSourceType.Accelerometer
private val ALTERNATIVE_ROTATION_ANGLE_SOURCE = RotationAngleSourceType.OrientationEventListener
