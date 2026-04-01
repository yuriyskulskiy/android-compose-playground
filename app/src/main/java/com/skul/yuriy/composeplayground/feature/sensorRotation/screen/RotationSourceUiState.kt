package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.RotationAngleSourceType

internal enum class RotationSourceUiState(
    val label: String,
    val sourceType: RotationAngleSourceType
) {
    RawSensor(
        label = "rawSensor",
        sourceType = RotationAngleSourceType.Accelerometer
    ),
    AngleListener(
        label = "angleListener",
        sourceType = RotationAngleSourceType.OrientationEventListener
    );

    fun next(): RotationSourceUiState = entries[(ordinal + 1) % entries.size]
}
