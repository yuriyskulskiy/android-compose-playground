package com.skul.yuriy.composeplayground.util.shadowborder;

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.multiLayersShadow(
    elevation: Dp,
    transparencyMultiplier: Float = 0.1f,
    color: Color = Color.Black,
    layers: Int = 20,
    shape: Shape = RoundedCornerShape(8.dp)
): Modifier = this.drawWithCache {

    // Set the shadow size based on the elevation
    val shadowSize =
        elevation.toPx() * 1.2f  // tweak the multiplier for proper shadow size
    val layerSize = shadowSize / layers

    // Create the outline based on the shape and size
    val outline = shape.createOutline(size, layoutDirection, this)
    val path = Path().apply { addOutline(outline) }

    onDrawWithContent {
        // Draw each shadow layer with decreasing opacity and expanding size
        repeat(layers) { layer ->
            val layerAlpha = 1f - (1 / layers.toFloat()) * layer
            val reducedLayerAlpha = layerAlpha * transparencyMultiplier

            // Adjust the scale factor based on the layer
            val scaleFactorX = 1f + (layer * layerSize) / size.width
            val scaleFactorY = 1f + (layer * layerSize) / size.height

            drawIntoCanvas { canvas ->
                // Save the current state of the canvas
                canvas.save()

                // Move the canvas to the center
                val centerX = size.width / 2
                val centerY = size.height / 2
                canvas.translate(centerX, centerY)

                // Apply scale transformation, scaling differently in X and Y directions
                canvas.scale(scaleFactorX, scaleFactorY)

                // Translate back to the original position
                canvas.translate(-centerX, -centerY)

                // Draw the outline using the path and apply transparency for the shadow effect
                drawPath(
                    path = path,
                    color = color.copy(alpha = reducedLayerAlpha),
                    style = Stroke(width = layerSize)  // Set stroke width for each layer
                )

                // Restore the canvas to its original state
                canvas.restore()
            }
        }

        drawContent()
    }
}


fun Modifier.shadowWithClippingShadowLayer(
    elevation: Dp,
    shape: Shape = CircleShape,
    spotColor: Color = DefaultShadowColor.copy(alpha = 0.25f) // More translucent spot shadow
): Modifier = this
    .drawWithCache {
        // Check if elevation is greater than 0
        if (elevation > 0.dp) {
            // Set the blur radius and offsets based on elevation
            val blurRadiusPx = elevation.toPx()  // Blur radius matches the elevation
            val dxPx = 0f                              // No horizontal offset
            val dyPx = (elevation * 0.5f).toPx() // Vertical offset is half the elevation

            // Create outline and path for the shape
            val outline = shape.createOutline(size, layoutDirection, this)
            val path = Path().apply { addOutline(outline) }

            // Create shadow paint with the calculated blur radius and offsets
            val shadowPaint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = false
                    setShadowLayer(blurRadiusPx, dxPx, dyPx, spotColor.toArgb())
                }
            }

            onDrawWithContent {
                // Clip the shadow, draw shadow, and then draw content
                clipPath(path, ClipOp.Difference) {
                    // Draw the shadow outside the shape
                    drawIntoCanvas { canvas ->
                        canvas.drawPath(path, shadowPaint)
                    }
                }

                // Draw the actual content inside the shape
                drawContent()
            }
        } else {
            // If elevation is 0.dp, just draw the content without the shadow
            onDrawWithContent {
                drawContent()
            }
        }
    }



fun Modifier.shadowWithClippingBlurMask(
    elevation: Dp,
    shape: Shape = RoundedCornerShape(8.dp),
    spotColor: Color = DefaultShadowColor,
    transparency: Float = 0.25f,              // adjust the transparency for more natural shadows
    elevationToBlurMultiplier: Float = 1.2f   // Match the blur radius to the elevation
): Modifier = this
    .drawWithCache {
        val transparentColor = spotColor.copy(alpha = transparency)
        val outline = shape.createOutline(size, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }

        // Use a more restrained blur radius, closer to the default shadow
        val blurRadius = elevation.toPx() * elevationToBlurMultiplier
        //apply light direction
        val dxPx = 0f  // No horizontal offset for default Material shadow
        val dyPx = (elevation * 0.5f).toPx()  // Vertical offset

        val shadowPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = false
                maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                color = transparentColor.toArgb()
            }
        }

        onDrawWithContent {
            drawIntoCanvas { canvas ->
                canvas.save()

                clipPath(path, ClipOp.Difference) {
                    canvas.translate(dxPx, dyPx)  // Apply vertical shadow offset
                    canvas.drawPath(path, shadowPaint)
                }
                canvas.restore()
            }
            drawContent()
        }
    }


// best solution based on https://gist.github.com/zed-alpha/3dc931720292c1f3ff31fa6a130f52cd
@Composable
fun ClippedShadow(
    elevation: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor
) {
    Layout(
        modifier
            .drawWithCache {
                val outline = shape.createOutline(size, layoutDirection, this)
                val path = Path().apply { addOutline(outline) }
                onDrawWithContent {
                    clipPath(path, ClipOp.Difference) {
                        this@onDrawWithContent.drawContent()
                    }
                }
            }
            .shadow(elevation, shape, false, ambientColor, spotColor)
    ) { _, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {}
    }
}


@Composable
fun ShadowBox(
    elevation: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
    interactiveMinimum: Boolean = false,
    wrappedTarget: @Composable () -> Unit
) {
    Layout(
        {
            ClippedShadow(elevation, Modifier, shape, ambientColor, spotColor)
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
                wrappedTarget
            )
        },
        when {
            interactiveMinimum -> modifier.minimumInteractiveComponentSize()
            else -> modifier
        }
    ) { measurables, constraints ->
        require(measurables.size == 2)

        val shadow = measurables[0]
        val target = measurables[1]

        val targetPlaceable = target.measure(constraints)
        val width = targetPlaceable.width
        val height = targetPlaceable.height

        val shadowPlaceable = shadow.measure(Constraints.fixed(width, height))

        layout(width, height) {
            shadowPlaceable.place(0, 0)
            targetPlaceable.place(0, 0)
        }
    }
}


