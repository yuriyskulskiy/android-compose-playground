package com.skul.yuriy.composeplayground.feature.sensorRotation.shape

import androidx.compose.ui.geometry.Offset

interface RotationShapeCalculatorContract {
    fun calculate(
        anchorA: Offset,
        anchorB: Offset,
        anchorC: Offset,
        anchorD: Offset,
        rotationDegrees: Float
    ): ShapePoints
}
