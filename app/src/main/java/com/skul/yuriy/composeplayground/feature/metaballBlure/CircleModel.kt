package com.skul.yuriy.composeplayground.feature.metaballBlure

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class CircleModel(
    var x: Float,
    var y: Float,
    val size: Dp,
    var velocityX: Float,
    var velocityY: Float
)

// Generate random circles with random size, position, and velocity
fun generateRandomCircles(
    count: Int,
    screenWidth: Int,
    screenHeight: Int,
    density: Density
): List<CircleModel> {
    return List(count) {
        val size = Random.nextInt(40, 160).dp
        val radiusPx = with(density) { size.toPx() / 2 }
        val x = Random.nextFloat() * (screenWidth - 2 * radiusPx) + radiusPx
        val y = Random.nextFloat() * (screenHeight - 2 * radiusPx) + radiusPx
        val velocityX = Random.nextFloat() * 10f + 1f
        val velocityY = Random.nextFloat() * 10f + 1f

        CircleModel(x = x, y = y, size = size, velocityX = velocityX, velocityY = velocityY)
    }
}