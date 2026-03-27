package com.skul.yuriy.composeplayground.feature.animatedRectButton

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurmask.drawOutlineBlurMaskShadow
import com.skul.yuriy.composeplayground.feature.animatedRectButton.snake.RectSnakeTrackPlacement
import com.skul.yuriy.composeplayground.feature.animatedRectButton.snake.rememberRectSnakeState
import com.skul.yuriy.composeplayground.feature.animatedRectButton.snake.rectSnakeBorder
import com.skul.yuriy.composeplayground.util.math.computeShadowOffset
import kotlinx.coroutines.delay

private const val HaloSpreadAnimationDurationMs = 300
private const val ShapeMorphAnimationDurationMs = 360
private const val TextMorphLongPressDelayMs = 500L

private class AnimatedRectButtonShapeState(
    val buttonHeight: Dp,
    val maxCornerRadius: Dp,
    val animatedButtonWidth: Dp,
    val buttonShape: Shape
)

@Composable
fun AnimatedRectButtonScreenContent(
    modifier: Modifier = Modifier,
    showDebugTrack: Boolean = true,
    trackPlacement: RectSnakeTrackPlacement = RectSnakeTrackPlacement.CENTER_ON_EDGE,
    shapeMode: RectButtonShapeMode = RectButtonShapeMode.ROUNDED_RECTANGLE
) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val defaultCornerFraction = 0f
    var topStartCornerFraction by rememberSaveable { mutableFloatStateOf(defaultCornerFraction) }
    var topEndCornerFraction by rememberSaveable { mutableFloatStateOf(defaultCornerFraction) }
    var bottomEndCornerFraction by rememberSaveable { mutableFloatStateOf(defaultCornerFraction) }
    var bottomStartCornerFraction by rememberSaveable { mutableFloatStateOf(defaultCornerFraction) }
    val shapeState = rememberAnimatedRectButtonShape(
        shapeMode = shapeMode,
        topStartCornerFraction = topStartCornerFraction,
        topEndCornerFraction = topEndCornerFraction,
        bottomEndCornerFraction = bottomEndCornerFraction,
        bottomStartCornerFraction = bottomStartCornerFraction
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
            maxValue = shapeState.maxCornerRadius
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedRectButtonDemo(
            shapeState = shapeState,
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
            maxValue = shapeState.maxCornerRadius
        )

        Text(
            modifier = Modifier.padding(top = 0.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
            color = Color.White,
            text = stringResource(R.string.effects_description_rect)
        )
    }
}

@Composable
private fun AnimatedRectButtonDemo(
    shapeState: AnimatedRectButtonShapeState,
    showDebugTrack: Boolean,
    trackPlacement: RectSnakeTrackPlacement,
    modifier: Modifier = Modifier
) {
    AnimatedRectBtnBox(
        modifier = modifier.size(
            width = shapeState.animatedButtonWidth,
            height = shapeState.buttonHeight
        ),
        onClick = {},
        mainColor = Color.Red,
        shape = shapeState.buttonShape,
        blurRadius = 4.dp,
        shadowOffsetSize = 8.dp,
        text = "TEST",
        showDebugTrack = showDebugTrack,
        trackPlacement = trackPlacement
    )
}

@Composable
private fun rememberAnimatedRectButtonShape(
    shapeMode: RectButtonShapeMode,
    topStartCornerFraction: Float,
    topEndCornerFraction: Float,
    bottomEndCornerFraction: Float,
    bottomStartCornerFraction: Float,
): AnimatedRectButtonShapeState {
    val circleButtonSize = 96.dp
    val rectangularButtonWidth = 188.dp
    val buttonHeight = 96.dp
    val maxCornerRadius = buttonHeight / 2
    val isCircleShape = shapeMode == RectButtonShapeMode.CIRCLE
    val topStartCornerTarget =
        if (isCircleShape) buttonHeight else maxCornerRadius * topStartCornerFraction
    val topEndCornerTarget =
        if (isCircleShape) buttonHeight else maxCornerRadius * topEndCornerFraction
    val bottomEndCornerTarget =
        if (isCircleShape) buttonHeight else maxCornerRadius * bottomEndCornerFraction
    val bottomStartCornerTarget =
        if (isCircleShape) buttonHeight else maxCornerRadius * bottomStartCornerFraction

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
    val animatedButtonWidth by animateDpAsState(
        targetValue = if (isCircleShape) circleButtonSize else rectangularButtonWidth,
        animationSpec = tween(durationMillis = ShapeMorphAnimationDurationMs),
        label = "animated_rect_button_width"
    )

    return AnimatedRectButtonShapeState(
        buttonHeight = buttonHeight,
        maxCornerRadius = maxCornerRadius,
        animatedButtonWidth = animatedButtonWidth,
        buttonShape = when (shapeMode) {
            RectButtonShapeMode.CIRCLE -> CircleShape
            RectButtonShapeMode.ROUNDED_RECTANGLE -> RoundedCornerShape(
                topStart = animatedTopStartCorner,
                topEnd = animatedTopEndCorner,
                bottomEnd = animatedBottomEndCorner,
                bottomStart = animatedBottomStartCorner
            )
        }
    )
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

    var isTextMorphActive by remember { mutableStateOf(false) }
    val snakeState = rememberRectSnakeState(enabled = !isPressed)
    val progressDegrees = snakeState.progress * 360f

    val shadowOffset = remember(progressDegrees, shadowOffsetSize) {
        val correctedAngle = correctionOffsetForTextPhase - progressDegrees
        computeShadowOffset(angleDegrees = correctedAngle, radius = shadowOffsetSize)
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(TextMorphLongPressDelayMs)
            isTextMorphActive = true
        } else {
            isTextMorphActive = false
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
                if (!isPressed && snakeState.isRunning) {
                    Modifier.rectSnakeBorder(
                        state = snakeState,
                        snakeLengthFraction = 0.75f,
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
            isMorphActive = isTextMorphActive,
            shadowOffset = shadowOffset,
            blurRadius = blurRadius
        )
    }
}
