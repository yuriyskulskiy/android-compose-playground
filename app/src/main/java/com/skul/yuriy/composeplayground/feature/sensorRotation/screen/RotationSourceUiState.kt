package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.RotationAngleSourceType

internal enum class RotationSourceUiState(
    val label: String,
    val sourceType: RotationAngleSourceType
) {
    RawSensor(
        label = "raw",
        sourceType = RotationAngleSourceType.Accelerometer
    ),
    AngleListener(
        label = "lis",
        sourceType = RotationAngleSourceType.OrientationEventListener
    );

    fun next(): RotationSourceUiState = entries[(ordinal + 1) % entries.size]
}
