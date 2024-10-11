package com.skul.yuriy.composeplayground.feature.animatedBorder.border

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.util.shadowborder.drawOutlineHaloShadowBlur


/**
 * An animated composable box wrapper that displays a circular shadow with a blurred halo effect.
 * The shadow appearance changes when the component is pressed, creating a dynamic glow around the box.
 *
 * The halo effect is implemented using a blurred outline, and the blur radius and halo border width
 * animate smoothly between different values depending on the component's pressed state.
 * This composable is especially useful for interactive elements that require a glowing effect.
 *
 * **Box Size Limitations:** Ensure that the size of the composable is large enough to accommodate
 * the halo effect. The combined size of `innerCircleContentSize` and `pressedHaloShadowWidth`
 * should be smaller than the composable's overall size to prevent clipping. This is due to the use of
 * `graphicsLayer` with `CompositingStrategy.Offscreen`, which restricts drawing outside the composable's bounds.
 *
 * **Warning about BlurMaskFilter:** Note that `BlurMaskFilter` may not behave consistently on
 * older Android API levels, particularly on API 26 (Android 8.0) and lower. On these devices,
 * the blur effect may not render as intended or could appear less smooth, resulting in inconsistent
 * halo effects. This limitation arises from platform-specific differences in how `BlurMaskFilter` is implemented.
 *
 * **Usage:** This composable can be used to create glowing buttons, cards, or other UI elements that
 * require a halo shadow effect to enhance visual feedback when pressed.
 *
 * @param modifier [Modifier] applied to the composable, allowing for customization of layout and appearance.
 * @param color The base color of the halo shadow. The opacity of this color will be adjusted
 *        to create the shadow effect.
 * @param initialBlurRadius The initial blur radius of the halo shadow when the component is not pressed.
 * @param pressedBlurRadius The blur radius of the halo shadow when the component is pressed.
 * @param initialHaloShadowWidth The initial width of the halo shadow border when the component is not pressed.
 * @param pressedHaloShadowWidth The width of the halo shadow border when the component is pressed.
 * @param innerCircleContentSize The size of the inner circle content. This defines the diameter
 *        of the central part inside the shadow.
 * @param interactionSource [MutableInteractionSource] that tracks the press state of the component,
 *        allowing animations to respond to user interaction.
 */
@Composable
fun BlurredCircularShadowBox(
    modifier: Modifier = Modifier,
    color: Color,
    initialBlurRadius: Dp,
    pressedBlurRadius: Dp,
    initialHaloShadowWidth: Dp,
    pressedHaloShadowWidth: Dp,
    innerCircleContentSize: Dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedBlurRadius by animateDpAsState(
        targetValue = if (isPressed) pressedBlurRadius else initialBlurRadius,
        animationSpec = tween(durationMillis = 300)
    )

    val animatedHaloBorderSize by animateDpAsState(
        targetValue = if (isPressed) pressedHaloShadowWidth else initialHaloShadowWidth,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .drawOutlineHaloShadowBlur(
                color = color.copy(alpha = 0.6f),
                blurRadius = animatedBlurRadius,
                haloBorderWidth = animatedHaloBorderSize,
                innerCircleContentSize = innerCircleContentSize
            )

            .then(
                if (isPressed) {
                    Modifier.drawOutlineHaloShadowBlur(
                        color = color,
                        blurRadius = 2.dp,
                        haloBorderWidth = 4.dp,
                        innerCircleContentSize = innerCircleContentSize
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                // Handle click if needed
            }
    )
}





