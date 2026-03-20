package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurmask.drawOutlineBlurMaskShadow
import com.skul.yuriy.composeplayground.util.math.computeShadowOffset
import com.skul.yuriy.composeplayground.util.shadowborder.RectSnakeTrackPlacement
import com.skul.yuriy.composeplayground.util.shadowborder.rectSnakeBorder

@Composable
fun AnimatedRectButtonScreenContent(
    modifier: Modifier = Modifier,
    showDebugTrack: Boolean = true,
    trackPlacement: RectSnakeTrackPlacement = RectSnakeTrackPlacement.CENTER_ON_EDGE
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        AnimatedRectBtnBox(
            modifier = Modifier
                .size(width = 188.dp, height = 96.dp),
            onClick = {},
            mainColor = Color.Red,
            blurRadius = 4.dp,
            shadowOffsetSize = 8.dp,
            text = "TEST",
            showDebugTrack = showDebugTrack,
            trackPlacement = trackPlacement
        )

        Text(
            modifier = Modifier.padding(top = 40.dp, start = 24.dp, end = 24.dp),
            color = Color.White,
            text = stringResource(R.string.effects_description_rect)
        )
    }
}

@Composable
fun AnimatedRectBtnBox(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    textSizeSp: Int = 34,
    shadowOffsetSize: Dp = 4.dp,
    blurRadius: Dp = 8.dp,
    mainColor: Color,
    textPressedColor: Color = Color.Black,
    correctionOffsetForTextPhase: Int = 120,
    cornerRadius: Dp = 24.dp,
    showDebugTrack: Boolean = true,
    trackPlacement: RectSnakeTrackPlacement = RectSnakeTrackPlacement.CENTER_ON_EDGE
) {
    val shape = RoundedCornerShape(cornerRadius)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val textColor = if (isPressed) textPressedColor else mainColor
    val pressedBackgroundColor = remember(mainColor) { lerp(mainColor, Color.Black, 0.18f) }
    val backgroundColor = if (isPressed) pressedBackgroundColor else Color.Transparent

    val initialHaloBorderWidth = 0.dp
    val pressedHaloBorderWidth = 42.dp

    val animatedSpread by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    var isRunning by remember { mutableStateOf(true) }
    var lastSavedProgress by remember { mutableStateOf(0f) }
    var animatedProgress by remember { mutableStateOf(0f) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    isRunning = false
                    lastSavedProgress = animatedProgress
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

    val infiniteTransition = if (isRunning && !isPressed) rememberInfiniteTransition() else null

    if (infiniteTransition != null) {
        val animatedProgressValue by infiniteTransition.animateFloat(
            initialValue = lastSavedProgress,
            targetValue = lastSavedProgress + 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = ""
        )
        animatedProgress = animatedProgressValue
    }

    val normalizedProgress = ((animatedProgress % 1f) + 1f) % 1f
    val progressDegrees = normalizedProgress * 360f

    val shadowOffset = remember(progressDegrees, shadowOffsetSize) {
        val correctedAngle = correctionOffsetForTextPhase - progressDegrees
        computeShadowOffset(angleDegrees = correctedAngle, radius = shadowOffsetSize)
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            lastSavedProgress = normalizedProgress
        }
    }

    Box(
        modifier = modifier
            .drawOutlineBlurMaskShadow(
                color = mainColor.copy(alpha = 0.5f),
                haloBorderWidth = animatedSpread,
                cornerRadius = cornerRadius,
                blurRadius = 16.dp
            )
            .background(
                color = backgroundColor,
                shape = shape
            )
            .then(
                if (showDebugTrack) {
                    Modifier.border(width = 1.dp, color = Color.White, shape = shape)
                } else {
                    Modifier
                }
            )
            .then(
                if (!isPressed && isRunning) {
                    Modifier.rectSnakeBorder(
                        snakeLengthFraction = 0.45f,
                        progress = normalizedProgress,
                        bodyColor = mainColor,
                        glowShadowColor = mainColor.copy(alpha = 0.8f),
                        cornerRadius = cornerRadius,
                        bodyStrokeWidth = 2.dp,
                        glowingShadowWidth = 16.dp,
                        trackPlacement = trackPlacement
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        GlowingText(
            text = text,
            textColor = textColor,
            textSizeSp = textSizeSp,
            isPressed = isPressed,
            shadowOffset = shadowOffset,
            blurRadius = blurRadius
        )
    }
}

@Composable
private fun GlowingText(
    text: String,
    textColor: Color,
    textSizeSp: Int,
    isPressed: Boolean,
    shadowOffset: Pair<Dp, Dp>,
    blurRadius: Dp
) {
    if (!isPressed) {
        Text(
            text = text,
            modifier = Modifier
                .offset(
                    x = shadowOffset.first,
                    y = shadowOffset.second
                )
                .blur(blurRadius),
            color = textColor.copy(alpha = 0.8f),
            fontSize = textSizeSp.sp
        )
    }

    Text(
        text = text,
        color = textColor,
        fontSize = textSizeSp.sp
    )
}
