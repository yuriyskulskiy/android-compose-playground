package com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing

interface IRotationAngleSmoother {
    val angle: Float

    fun onRawAngle(rawAngle: Float)

    fun reset()
}
