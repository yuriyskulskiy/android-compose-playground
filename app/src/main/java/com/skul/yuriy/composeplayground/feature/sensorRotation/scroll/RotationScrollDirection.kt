package com.skul.yuriy.composeplayground.feature.sensorRotation.scroll

import com.skul.yuriy.composeplayground.feature.sensorRotation.smoothing.normalizeAnchoringDegrees

internal fun isRotationScrollDirectionInverted(angleDegrees: Float): Boolean {
    val normalizedAngle = normalizeAnchoringDegrees(angleDegrees)
    return normalizedAngle in 90f..180f || normalizedAngle in -180f..-90f
}
