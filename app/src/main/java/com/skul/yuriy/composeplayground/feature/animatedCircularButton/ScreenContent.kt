package com.skul.yuriy.composeplayground.feature.animatedCircularButton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.skul.yuriy.composeplayground.util.shadowborder.developSnake
import com.skul.yuriy.composeplayground.util.shadowborder.drawOutlineCircularShadowGradient

@Composable
fun AnimatedCircleButtonScreenContent(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AnimatedCircularBtnBox(
            modifier = Modifier
                .size(112.dp),
            onClick = {}
        )
    }
}

@Composable
fun AnimatedCircularBtnBox(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val mainColor = Color.Green

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val iconTintColor = if (isPressed) (Color.Black) else (mainColor)
    val backgroundColor = if (isPressed) mainColor else Color.Transparent

    // glowing shadow
    val initialHaloBorderWidth = 0.dp
    val pressedHaloBorderWidth = 48.dp

    val animatedSpread by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 300)
    )

    //rotation arc
    var isRunning by remember { mutableStateOf(true) }
    var lastSavedAngle by remember { mutableStateOf(0f) }
    var animatedAngle by remember { mutableStateOf(0f) }
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
        modifier = modifier
            .drawOutlineCircularShadowGradient(
                color = mainColor.copy(alpha = 0.6f),
                haloBorderWidth = animatedSpread,
            )
            .then(
                if (isPressed) {
                    //add optionally small lighting ring
                    Modifier.drawOutlineCircularShadowGradient(
                        color = Color.Green,
                        haloBorderWidth = 4.dp,
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (!isPressed && isRunning) {
                    Modifier.developSnake(
                        rotationDegrees = animatedAngle,
                        bodyColor = mainColor,
                        glowShadowColor = mainColor.copy(alpha = 0.6f),
                        bodyStrokeWidth = 2.dp,
                        glowingShadowWidth = 12.dp,
//                        glowingBlurRadius = blurRadius
                    )
                } else Modifier
            )

            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null // Remove the ripple effect
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(56.dp),
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = iconTintColor
        )
    }
}