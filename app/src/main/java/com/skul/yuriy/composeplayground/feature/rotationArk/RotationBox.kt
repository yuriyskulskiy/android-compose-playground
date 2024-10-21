package com.skul.yuriy.composeplayground.feature.rotationArk;

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.skul.yuriy.composeplayground.util.shadowborder.ArcPaddingType
import com.skul.yuriy.composeplayground.util.shadowborder.developSnake


@Composable
fun RotationBox(
    modifier: Modifier = Modifier,
    bodyColor: Color = Color.Red,
    glowShadowColor: Color = Color.Red.copy(alpha = 0.9f),
    selectedPaddingType: ArcPaddingType,
    innerBodyWidth: Dp,
    shadowWidth: Dp,
    blurRadius: Dp,
    content: @Composable (BoxScope.(interactionSource: MutableInteractionSource) -> Unit),
) {


    var isRunning by remember { mutableStateOf(true) }
    var lastSavedAngle by remember { mutableStateOf(0f) }
    var animatedAngle by remember { mutableStateOf(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    isRunning = false
                    lastSavedAngle = animatedAngle
                }

                Lifecycle.Event.ON_RESUME -> {
                    isRunning = true
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Rotation animation only when the app is active and the button is not pressed
    val infiniteTransition = if (isRunning && !isPressed) rememberInfiniteTransition() else null

    // If animation is active - perform rotation angel state updates
    if (infiniteTransition != null) {
        val animatedAngleValue by infiniteTransition.animateFloat(
            initialValue = lastSavedAngle,
            targetValue = lastSavedAngle + 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = ""
        )
        animatedAngle = animatedAngleValue
    }

    //Store th rotation angle when press
    LaunchedEffect(isPressed) {
        if (isPressed) {
            lastSavedAngle = animatedAngle
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Remove ripple effect if not needed
                onClick = {} // Handle onClick logic if needed
            )
            .then(
                if (!isPressed && isRunning) {
                    Modifier.developSnake(
                        rotationDegrees = animatedAngle,
                        bodyColor = bodyColor,
                        glowShadowColor = glowShadowColor,
                        arcPaddingType = selectedPaddingType,
                        bodyStrokeWidth = innerBodyWidth,
                        glowingShadowWidth = shadowWidth,
                        glowingBlurRadius = blurRadius
                    )
                } else Modifier
            )
    ) {
        if (isPressed) {
            content(interactionSource)
        }
    }
}




