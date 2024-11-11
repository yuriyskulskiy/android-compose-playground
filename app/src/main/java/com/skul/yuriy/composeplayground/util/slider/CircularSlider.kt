package com.skul.yuriy.composeplayground.util.slider

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun Modifier.satelliteDotDraggable(
    angleState: MutableState<Float>,
    dotColor: Color = Color.Green,
    dotRadius: Dp = 8.dp,
    circularSliderArcRadius: Dp,
    blurRadius: Dp = 4.dp,
): Modifier = this.then(
    Modifier
        .pointerInput(Unit) {
            val center = Offset(size.width / 2f, size.height / 2f)

            detectDragGestures(
                onDrag = { change, _ ->
                    // Calculate the angle based on the drag position relative to the center
                    val dragAngleRadians = atan2(
                        y = -(change.position.y - center.y),
                        x = change.position.x - center.x
                    )
                    var newAngleDegrees = Math
                        .toDegrees(dragAngleRadians.toDouble())
                        .toFloat()

                    // Ensure angle is within 0 to 360 degrees
                    if (newAngleDegrees < 0) newAngleDegrees += 360
                    angleState.value = newAngleDegrees
                    change.consume()
                }
            )
        }
        .drawWithCache {
            val dotRadiusPx = dotRadius.toPx()
            val parentRadiusPx = circularSliderArcRadius.toPx()
            val smallDotOffsetRadius = parentRadiusPx - 2 * dotRadiusPx
            val blurRadiusPx = blurRadius.toPx()

            // Set up the Paint for the dot with blur effect
            val dotPaint = Paint().apply {
                color = dotColor
                isAntiAlias = true
                asFrameworkPaint().apply {
                    maskFilter = BlurMaskFilter(blurRadiusPx, BlurMaskFilter.Blur.NORMAL)
                }
            }

            onDrawBehind {
                // Convert the updated angle to radians for position calculation
                val angleRadians = Math.toRadians(angleState.value.toDouble())

                // Calculate dot position based on the current angle
                val dotX = size.width / 2 + smallDotOffsetRadius * cos(angleRadians).toFloat()
                val dotY = size.height / 2 - smallDotOffsetRadius * sin(angleRadians).toFloat()

                drawIntoCanvas { canvas ->
                    // Draw the dot at the calculated position with blur effect
                    canvas.nativeCanvas.drawCircle(
                        dotX,
                        dotY,
                        dotRadiusPx,
                        dotPaint.asFrameworkPaint()
                    )
                }
            }
        }
)