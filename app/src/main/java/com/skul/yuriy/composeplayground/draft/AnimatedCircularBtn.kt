package com.skul.yuriy.composeplayground.draft

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.skul.yuriy.composeplayground.util.cornerRedLinearGradient2

//Draft
@Composable
fun AnimatedCircularBtnScreen() {
    Box(
        Modifier
            .fillMaxSize()
//            .background(Color.White),
            .background(brush = cornerRedLinearGradient2())
            .padding(top = 200.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CardWithAnimatedNeonArcBorder_(
            modifier = Modifier
                .size(100.dp, 100.dp)

        ) { interactionSource ->
            val isPressed by interactionSource.collectIsPressedAsState()
            // Animate the background color
            val backgroundColor by animateColorAsState(
                targetValue = if (isPressed) Color.Green else Color.Transparent,
                animationSpec = tween(durationMillis = 150) // Adjust the duration as needed
            )

            // Animate the icon tint color
            val iconTintColor by animateColorAsState(
                targetValue = if (isPressed) Color.Black else Color.Green,
                animationSpec = tween(durationMillis = 300) // Adjust the duration as needed
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = backgroundColor, // Transparent background for the Box
                        shape = CircleShape
                    )
                    .clip(CircleShape) // Ensure the Box has a circular shape
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null // Remove the ripple effect
                    ) {
                        // Handle the click event here
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.Add, // Example icon, you can use any
                    contentDescription = "Favorite",
                    tint = iconTintColor // Use the animated tint color
                )
            }
        }
    }
}


@Composable
fun CardWithAnimatedNeonArcBorder_(
    modifier: Modifier = Modifier,
    color: Color = Color.Green,

    onCardClick: () -> Unit = {},
    content: @Composable (interactionSource: MutableInteractionSource) -> Unit // Pass the InteractionSource to content
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = -360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )


    val myBrush = Brush.sweepGradient(

        0.0f to color.copy(alpha = 0f), //fix issue with concatenated rounded caps color
        0.25f to color.copy(alpha = 0f),
        0.75f to color.copy(alpha = 1f),
    )
    val strokeWidthDp = 4.dp
    val interactionSource = remember { MutableInteractionSource() }
    // State to track if the button is pressed
    val isPressed by interactionSource.collectIsPressedAsState()

    val animationProgress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 300) // Adjust the animation duration as needed
    )

    // Map the fraction to the desired blur and spread values
    val blur = lerp(0.dp, 24.dp, animationProgress)
    val spread = lerp(0.dp, 16.dp, animationProgress)
    Surface(
        modifier = modifier
            .graphicsLayer {
                // Disable clipping so the halo can extend outside the bounds
                compositingStrategy = CompositingStrategy.ModulateAlpha
                clip = false
            }
            .drawOutlineCircularShadow(Color.Green, blur = blur, haloBorderWidth = spread)

//            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onCardClick()
            }
            .padding(0.dp)
            .drawWithContent {
                if (!isPressed) {
                    rotate(angle) {
                        val strokeWidth = strokeWidthDp.toPx()
                        drawArc(
                            brush = myBrush,
                            startAngle = 90f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                            ),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            topLeft = Offset(
                                strokeWidth / 2, strokeWidth / 2
                            )
                        )
                    }
                }
                if (isPressed) {
//                    val haloBrush = Brush.radialGradient(
//                        colorStops = arrayOf(
////                            0.0f to Color.Green.copy(alpha = 1.0f), // Center, fully opaque green
////                            0.49f to Color.Green.copy(alpha = 1f), // 30% of the radius, semi-transparent green
//                            0.60f to Color.Green.copy(alpha = 0.5f), // 70% of the radius, more transparent
//                            1.0f to Color.Green.copy(alpha = 0f)    // Edge, fully transparent
//                        ),
//                        center = center,
//                        radius = 50.dp.toPx() + 50.dp.toPx(),
//                    )
//                    drawCircle(
//                        brush = haloBrush,
////                        color = Color.Red,
//                        radius = 50.dp.toPx(),
//                        center = center
//                    )
                }
                drawContent()
            },
        color = Color.Transparent, // Ensure the background is transparent
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.padding(if (isPressed) 0.dp else strokeWidthDp),
            contentAlignment = Alignment.Center

        ) {
            content(interactionSource)
        }
    }
}