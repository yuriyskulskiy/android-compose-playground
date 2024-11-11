package com.skul.yuriy.composeplayground.util.math

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Computes the horizontal and vertical offsets for a shadow based on the provided angle and radius.
 *
 * This function converts the given angle from degrees to radians, then calculates the x and y
 * offsets using trigonometric functions to position the shadow at a specified distance and angle
 * from the origin. The resulting offsets can be applied to a shadow or other visual element.
 *
 * @param angleDegrees The angle in degrees at which to position the shadow, with 0 degrees being
 *                     aligned horizontally to the right, and increasing counterclockwise.
 * @param radius       The distance (or "radius") from the origin to position the shadow.
 * @return             A pair of [Dp] values representing the horizontal (x) and vertical (y)
 *                     offsets for positioning the shadow. The x offset is negative to align
 *                     with typical coordinate systems.
 */
fun computeShadowOffset(angleDegrees: Float, radius: Dp): Pair<Dp, Dp> {
    val angleRadians = Math.toRadians(angleDegrees.toDouble())
    val xOffset = -(cos(angleRadians) * radius.value).dp
    val yOffset = (sin(angleRadians) * radius.value).dp // Negate y for correct orientation

    return Pair(xOffset, yOffset)
}