package com.skul.yuriy.composeplayground.feature.animatedRectButton

import android.content.res.Configuration
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurmask.drawOutlineBlurMaskShadow
import com.skul.yuriy.composeplayground.util.math.computeShadowOffset
import com.skul.yuriy.composeplayground.feature.animatedRectButton.snake.RectSnakeTrackPlacement
import com.skul.yuriy.composeplayground.feature.animatedRectButton.snake.rectSnakeBorder

/**
 * Wraps any progress value into the stable [0, 1) interval.
 *
 * The double modulo form is intentional: a plain `% 1f` can stay negative for negative inputs,
 * while this version normalizes both positive overflow and negative values into the same cyclic
 * progress range used by the snake animation.
 */
private fun normalizeProgress(progress: Float): Float =
    ((progress % 1f) + 1f) % 1f

private const val HaloSpreadAnimationDurationMs = 300
private const val SnakeLoopAnimationDurationMs = 3500
private const val ShapeMorphAnimationDurationMs = 360

@Composable
fun AnimatedRectButtonScreenContent(
    modifier: Modifier = Modifier,
    showDebugTrack: Boolean = true,
    trackPlacement: RectSnakeTrackPlacement = RectSnakeTrackPlacement.CENTER_ON_EDGE,
    shapeMode: RectButtonShapeMode = RectButtonShapeMode.ROUNDED_RECTANGLE
) {
    val circleButtonSize = 96.dp
    val rectangularButtonWidth = 188.dp
    val buttonHeight = 96.dp
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val maxCornerRadius = buttonHeight / 2
    val defaultCornerFraction = 0f
    var topStartCornerFraction by remember { mutableFloatStateOf(defaultCornerFraction) }
    var topEndCornerFraction by remember { mutableFloatStateOf(defaultCornerFraction) }
    var bottomEndCornerFraction by remember { mutableFloatStateOf(defaultCornerFraction) }
    var bottomStartCornerFraction by remember { mutableFloatStateOf(defaultCornerFraction) }
    val isCircleShape = shapeMode == RectButtonShapeMode.CIRCLE
    val topStartCornerTarget = if (isCircleShape) buttonHeight else maxCornerRadius * topStartCornerFraction
    val topEndCornerTarget = if (isCircleShape) buttonHeight else maxCornerRadius * topEndCornerFraction
    val bottomEndCornerTarget = if (isCircleShape) buttonHeight else maxCornerRadius * bottomEndCornerFraction
    val bottomStartCornerTarget = if (isCircleShape) buttonHeight else maxCornerRadius * bottomStartCornerFraction
    val animatedTopStartCorner by animateDpAsState(
        targetValue = topStartCornerTarget,
        animationSpec = tween(durationMillis = ShapeMorphAnimationDurationMs),
        label = "animated_top_start_corner"
    )
    val animatedTopEndCorner by animateDpAsState(
        targetValue = topEndCornerTarget,
        animationSpec = tween(durationMillis = ShapeMorphAnimationDurationMs),
        label = "animated_top_end_corner"
    )
    val animatedBottomEndCorner by animateDpAsState(
        targetValue = bottomEndCornerTarget,
        animationSpec = tween(durationMillis = ShapeMorphAnimationDurationMs),
        label = "animated_bottom_end_corner"
    )
    val animatedBottomStartCorner by animateDpAsState(
        targetValue = bottomStartCornerTarget,
        animationSpec = tween(durationMillis = ShapeMorphAnimationDurationMs),
        label = "animated_bottom_start_corner"
    )
    val animatedRectShape = RoundedCornerShape(
        topStart = animatedTopStartCorner,
        topEnd = animatedTopEndCorner,
        bottomEnd = animatedBottomEndCorner,
        bottomStart = animatedBottomStartCorner
    )
    val animatedButtonWidth by animateDpAsState(
        targetValue = if (isCircleShape) circleButtonSize else rectangularButtonWidth,
        animationSpec = tween(durationMillis = ShapeMorphAnimationDurationMs),
        label = "animated_rect_button_width"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.then(
            if (isPortrait) {
                // Keep portrait non-scrollable: the scroll container noticeably degrades the tap
                // animation response because press handling via InteractionSource gets delayed
                // inside vertically scrollable content.
                Modifier
            } else {
                Modifier.verticalScroll(scrollState)
            }
        )
    ) {
        CornerSliderPairRow(
            startCorner = CornerSliderCorner.TopStart,
            startValue = topStartCornerFraction,
            onStartValueChange = { topStartCornerFraction = it },
            endCorner = CornerSliderCorner.TopEnd,
            endValue = topEndCornerFraction,
            onEndValueChange = { topEndCornerFraction = it },
            maxValue = maxCornerRadius
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedRectBtnBox(
            modifier = Modifier
                .size(width = animatedButtonWidth, height = buttonHeight),
            onClick = {},
            mainColor = Color.Red,
            shape = animatedRectShape,
            blurRadius = 4.dp,
            shadowOffsetSize = 8.dp,
            text = "TEST",
            showDebugTrack = showDebugTrack,
            trackPlacement = trackPlacement
        )

        Spacer(modifier = Modifier.height(16.dp))

        CornerSliderPairRow(
            startCorner = CornerSliderCorner.BottomStart,
            startValue = bottomStartCornerFraction,
            onStartValueChange = { bottomStartCornerFraction = it },
            endCorner = CornerSliderCorner.BottomEnd,
            endValue = bottomEndCornerFraction,
            onEndValueChange = { bottomEndCornerFraction = it },
            maxValue = maxCornerRadius
        )

        Text(
            modifier = Modifier.padding(top = 0.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
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
    cornerRadius: Dp = 8.dp,
    shape: Shape? = null,
    showDebugTrack: Boolean = true,
    trackPlacement: RectSnakeTrackPlacement = RectSnakeTrackPlacement.CENTER_ON_EDGE
) {
    val resolvedShape = shape ?: RoundedCornerShape(cornerRadius)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val textColor = if (isPressed) textPressedColor else mainColor
    val pressedBackgroundColor = remember(mainColor) { lerp(mainColor, Color.Black, 0.18f) }
    val backgroundColor = if (isPressed) pressedBackgroundColor else Color.Transparent

    val initialHaloBorderWidth = 0.dp
    val pressedHaloBorderWidth = 42.dp

    val animatedSpread by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = HaloSpreadAnimationDurationMs),
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
                animation = tween(SnakeLoopAnimationDurationMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = ""
        )
        animatedProgress = animatedProgressValue
    }

    val normalizedProgress = normalizeProgress(animatedProgress)
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
                shape = resolvedShape,
                blurRadius = 16.dp
            )
            .background(
                color = backgroundColor,
                shape = resolvedShape
            )
            .then(
                if (showDebugTrack) {
                    Modifier.border(width = 1.dp, color = Color.White, shape = resolvedShape)
                } else {
                    Modifier
                }
            )
            .then(
                if (!isPressed && isRunning) {
                    Modifier.rectSnakeBorder(
                        snakeLengthFraction = 0.75f,
                        progress = normalizedProgress,
                        bodyColorFrom = mainColor.copy(alpha = 0f),
                        bodyColorTo = mainColor,
                        glowColorFrom = mainColor.copy(alpha = 0f),
                        glowColorTo = mainColor.copy(alpha = 0.9f),
                        cornerRadius = cornerRadius,
                        shape = resolvedShape,
                        bodyStrokeWidth = 2.dp,
                        glowingShadowWidth = 8.dp,
                        trackPlacement = trackPlacement
                    )
                } else {
                    Modifier
                }
            )
            .clip(resolvedShape)
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
