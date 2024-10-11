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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.util.shadowborder.drawOutlineCircularShadowGradient

/**
 * An animated composable box wrapper that displays a gradient halo border.
 * The halo border's width animates between different values when the component is pressed,
 * creating a visually appealing gradient shadow effect around the box.
 *
 * The halo effect is implemented using a gradient with varying alpha values to simulate
 * a blurred edge. When the box is pressed, an optional additional lighting ring can be added
 * for a more dynamic appearance.
 *
 * **Usage:** This composable can be used to create interactive UI elements with animated
 * halo effects, especially for buttons or cards that need a glowing appearance when pressed.
 *
 * @param modifier [Modifier] applied to the composable, allowing for customization of layout and appearance.
 * @param color The base color of the gradient halo. The opacity of this color will be adjusted
 *        to create the shadow effect.
 * @param initialHaloBorderWidth The initial width of the halo border when the component is not pressed.
 * @param pressedHaloBorderWidth The width of the halo border when the component is pressed.
 * @param interactionSource [MutableInteractionSource] that tracks the press state of the component,
 *        allowing animations to respond to user interaction.
 */
@Composable
fun GradientCircularShadowBox(
    modifier: Modifier = Modifier,
    color: Color,
    initialHaloBorderWidth: Dp,
    pressedHaloBorderWidth: Dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedSpread by animateDpAsState(
        targetValue = if (isPressed) pressedHaloBorderWidth else initialHaloBorderWidth,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            //animated halo shadow
            .drawOutlineCircularShadowGradient(
                color = color.copy(alpha = 0.6f),
                haloBorderWidth = animatedSpread,
            )

            .then(
                if (isPressed) {
                    //add optionally small lighting ring
                    Modifier.drawOutlineCircularShadowGradient(
                        color = color,
                        haloBorderWidth = 4.dp,
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null  //disable ripple
            ) {
                // Handle click if needed
            }
    )
}

