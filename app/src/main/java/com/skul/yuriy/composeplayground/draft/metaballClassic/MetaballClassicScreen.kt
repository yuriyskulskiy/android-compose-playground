package com.skul.yuriy.composeplayground.draft.metaballClassic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.regularComponents.AdaptiveSquareBoxBasedOnOrientation
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar
import kotlinx.coroutines.delay

@Composable
fun MetaballMathScreen() {
    val navController: NavController = LocalNavController.current
    Column(Modifier.fillMaxSize()) {
        CustomTopAppBar(
            title = stringResource(R.string.metaballs_classic_approach),
            onNavUp = { navController.navigateUp() })
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedMetaballGrid()
        }
    }
}


@Composable
fun AnimatedMetaballGrid(gridSize: Int = 100) {
    val random = kotlin.random.Random

    // Generate random initial positions and velocities
    var circle1State by remember {
        mutableStateOf(
            MovingCircleState(
                position = CellOffset(random.nextInt(0, gridSize), random.nextInt(0, gridSize)),
                velocity = randomDirection()
            )
        )
    }
    var circle2State by remember {
        mutableStateOf(
            MovingCircleState(
                position = CellOffset(random.nextInt(0, gridSize), random.nextInt(0, gridSize)),
                velocity = randomDirection()
            )
        )
    }
    var circle3State by remember {
        mutableStateOf(
            MovingCircleState(
                position = CellOffset(random.nextInt(0, gridSize), random.nextInt(0, gridSize)),
                velocity = randomDirection()
            )
        )
    }

    // Radius of the metaballs
    val influenceRadius = (gridSize * 0.1).toInt()
    val bigInfluenceRadius = influenceRadius * 2 // Twice the size for one ball
    val threshold = 1.0f

    // State to control whether the animation is running
    val isRunning = remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Track lifecycle events to control `isRunning`
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            isRunning.value = event == Lifecycle.Event.ON_RESUME // Run only when the screen is active
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer) // Clean up observer
        }
    }

    // Animation loop
    LaunchedEffect(isRunning.value) {
        while (isRunning.value) {
            circle1State = circle1State.moveAndReflect(gridSize)
            circle2State = circle2State.moveAndReflect(gridSize)
            circle3State = circle3State.moveAndReflect(gridSize)

            // Wait before the next tick
            delay(50L) // Adjust delay for smoother or slower movement
        }
    }

    // Draw the grid and metaballs
    AdaptiveSquareBoxBasedOnOrientation(
        modifier = Modifier
            .padding(1.dp)
            .border(2.dp, Color.Gray)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = size.width / gridSize
            val cellHeight = size.height / gridSize
            val overlap = 0.5f // Slight overlap to eliminate gaps

            // **1. Draw Red Grid Lines**
            for (gridX in 0..gridSize) {
                val x = gridX * cellWidth
                drawLine(
                    color = Color.Gray,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
            }
            for (gridY in 0..gridSize) {
                val y = gridY * cellHeight
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            // **2. Draw Metaball Shapes**
            for (gridX in 0 until gridSize) {
                for (gridY in 0 until gridSize) {
                    val topLeftX = gridX * cellWidth
                    val topLeftY = gridY * cellHeight

                    val cellCenter = CellOffset(gridX, gridY)
                    val fieldValue = calculateMetaballField(
                        cellCenter,
                        circle1State.position, influenceRadius, // Normal-sized ball
                        circle2State.position, influenceRadius, // Normal-sized ball
                        circle3State.position, bigInfluenceRadius // Twice the size
                    )

                    if (fieldValue > threshold) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(topLeftX, topLeftY),
                            size = Size(cellWidth + overlap, cellHeight + overlap)
                        )
                    }
                }
            }
        }
    }
}

// Data class for grid cell offsets
data class CellOffset(val x: Int, val y: Int) {
    operator fun plus(other: CellOffset): CellOffset {
        return CellOffset(x + other.x, y + other.y)
    }

    operator fun times(scalar: Int): CellOffset {
        return CellOffset(x * scalar, y * scalar)
    }
}

// Class to represent a moving circle's state
data class MovingCircleState(val position: CellOffset, val velocity: CellOffset) {
    fun moveAndReflect(gridSize: Int): MovingCircleState {
        var newVelocity = velocity

        // Reflect if hitting a boundary
        if (position.x + velocity.x < 0 || position.x + velocity.x >= gridSize) {
            newVelocity = newVelocity.copy(x = -newVelocity.x)
        }
        if (position.y + velocity.y < 0 || position.y + velocity.y >= gridSize) {
            newVelocity = newVelocity.copy(y = -newVelocity.y)
        }

        // Move the circle
        val newPosition = position + newVelocity
        return MovingCircleState(newPosition, newVelocity)
    }
}

// Function to calculate metaball field for three circles with different radii
fun calculateMetaballField(
    point: CellOffset,
    circle1Position: CellOffset, circle1Radius: Int,
    circle2Position: CellOffset, circle2Radius: Int,
    circle3Position: CellOffset, circle3Radius: Int
): Float {
    val field1 = (circle1Radius * circle1Radius).toFloat() / distanceSquared(point, circle1Position)
    val field2 = (circle2Radius * circle2Radius).toFloat() / distanceSquared(point, circle2Position)
    val field3 = (circle3Radius * circle3Radius).toFloat() / distanceSquared(point, circle3Position)
    return field1 + field2 + field3
}

// Function to calculate squared distance between two points
fun distanceSquared(point1: CellOffset, point2: CellOffset): Float {
    val dx = (point1.x - point2.x).toFloat()
    val dy = (point1.y - point2.y).toFloat()
    return dx * dx + dy * dy
}

// Function to generate a random direction vector
fun randomDirection(): CellOffset {
    val random = kotlin.random.Random
    val dx = if (random.nextBoolean()) 1 else -1 // Randomly choose -1 or 1 for x
    val dy = if (random.nextBoolean()) 1 else -1 // Randomly choose -1 or 1 for y
    return CellOffset(dx, dy)
}
