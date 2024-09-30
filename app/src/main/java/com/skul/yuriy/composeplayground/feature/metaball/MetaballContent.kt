package com.skul.yuriy.composeplayground.feature.metaball

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.android.awaitFrame

@RequiresApi(Build.VERSION_CODES.S)
val metaBallRenderEffect =
    RenderEffect.createChainEffect(
        RenderEffect.createColorFilterEffect(
            ColorMatrixColorFilter(
                ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f, 0f,
                        0f, 0f, 1f, 0f, 0f,
                        0f, 0f, 0f, 160f, -10000f
                    )
                )
            )
        ),
        RenderEffect.createBlurEffect(100f, 100f, Shader.TileMode.MIRROR)
    ).asComposeRenderEffect()

@Composable
fun MovingCirclesWithMetaballEffect(circleCount: Int = 15) {
    var boxSize by remember { mutableStateOf(IntSize(0, 0)) }
    var circles by remember { mutableStateOf(listOf<CircleModel>()) }
    val density = LocalDensity.current

    val lifecycleOwner = LocalLifecycleOwner.current
    var isAnimationRunning by remember { mutableStateOf(true) }

    // Observe lifecycle to control animation only when "screen is resumed"
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isAnimationRunning = when (event) {
                Lifecycle.Event.ON_RESUME -> true
                Lifecycle.Event.ON_PAUSE -> false
                else -> isAnimationRunning
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(boxSize) {
        if (boxSize.width > 0 && boxSize.height > 0) {
            circles = generateRandomCircles(circleCount, boxSize.width, boxSize.height, density)
        }
    }

    LaunchedEffect(circles, isAnimationRunning) {
        while (isAnimationRunning) {
            awaitFrame()

            circles = circles.map { circle ->
                val radiusPx = with(density) { circle.size.toPx() / 2 }
                val updatedX = circle.x + circle.velocityX
                val updatedY = circle.y + circle.velocityY

                val newVelocityX =
                    if (updatedX - radiusPx < 0 || updatedX + radiusPx > boxSize.width) {
                        -circle.velocityX
                    } else {
                        circle.velocityX
                    }

                val newVelocityY =
                    if (updatedY - radiusPx < 0 || updatedY + radiusPx > boxSize.height) {
                        -circle.velocityY
                    } else {
                        circle.velocityY
                    }

                circle.copy(
                    x = updatedX,
                    y = updatedY,
                    velocityX = newVelocityX,
                    velocityY = newVelocityY
                )
            }
        }
    }

    val renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        metaBallRenderEffect
    } else {
        throw UnsupportedOperationException("Unsupported Android version.")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates -> boxSize = coordinates.size }
    ) {
        Canvas(
            modifier =
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.renderEffect = renderEffect
                }
        ) {
            circles.forEach { circle ->
                val radiusPx = with(density) { circle.size.toPx() / 2 }

                // Draw the circles
                drawCircle(
                    color = Color.White,
                    radius = radiusPx,
                    center = Offset(circle.x, circle.y)
                )
            }
        }
    }
}
