package com.skul.yuriy.composeplayground.draft.trasnparenContentButtomEdgeShadow

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.ui.theme.PinkVeryLight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RectangleShape(
    navigateUp: () -> Unit
) {


    BottomEdgeShadowBox(
        elevation = 40.dp,
        modifier = Modifier,
        shape = RectangleShape,
        interactiveMinimum = false
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors()
                .copy(containerColor = PinkVeryLight),
            modifier = Modifier,
            title = { Text("Rectangle bottom edge") },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
            },
        )
    }
}


@Composable
fun BottomEdgeShadowBox(
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
            ClippedBottomEdgeShadow(elevation, Modifier, shape, ambientColor, spotColor)
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

@Composable
fun ClippedBottomEdgeShadow(
    elevation: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor
) {
    Layout(
        modifier
            .drawWithCache {
                val elevationPx = elevation.toPx()
                // Dynamic shadow offsets based on elevation
                // Dynamic offsets for shadow parts
                val topOffsetPx = elevationPx * 0.4f // Small offset for top
                val leftRightOffsetPx = elevationPx * 1.4f // Moderate offset for left and right
                val bottomOffsetPx = elevationPx * 2.2f // Larger offset for bottom

                // Adjust the size by increasing the width (left + right) and height (top)
                val adjustedSize = Size(
                    width = size.width + 2f * leftRightOffsetPx, // Add offset to both sides (left and right)
                    height = size.height + topOffsetPx // Only affect bottom for shadow extension
                )

                val offsetLeftTop = Offset(-leftRightOffsetPx, -topOffsetPx) // Move left and up

                // Create the outline with the adjusted size
                val outline = shape.createOutline(adjustedSize, layoutDirection, this)

                val path = Path().apply {
                    addOutline(outline) // Use the outline with the adjusted size
                    translate(offsetLeftTop) // Translate by left and top offsets
                }

                // Create the left-side path dynamically
                val outlineHeight = size.height
                val outlineWidth = size.width
                val leftSidePath = createSidePath(
                    outlineHeight = outlineHeight,
                    outlineWidth = outlineWidth,
                    horizontalOffsetPx = leftRightOffsetPx,
                    verticalBottomOffsetPx = bottomOffsetPx,
                    isLeft = true
                )

                // Create the right-side path dynamically
                val rightSidePath = createSidePath(
                    outlineHeight = outlineHeight,
                    outlineWidth = outlineWidth,
                    horizontalOffsetPx = leftRightOffsetPx,
                    verticalBottomOffsetPx = bottomOffsetPx,
                    isLeft = false
                )

                // Combine the main path, left, and right paths
                val combinedPath = path
                    .plus(leftSidePath)
                    .plus(rightSidePath)

                onDrawWithContent {
                    // Clip the content where the shadow will be drawn
                    clipPath(combinedPath, ClipOp.Difference) {
                        this@onDrawWithContent.drawContent()
                    }
                }
            }
            .shadow(elevation, shape, false, ambientColor, spotColor) // Standard shadow
    ) { _, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {}
    }
}

// Function to create the side paths with dynamic offsets
fun createSidePath(
    outlineHeight: Float,
    outlineWidth: Float,
    horizontalOffsetPx: Float,
    verticalBottomOffsetPx: Float,
    isLeft: Boolean // Determines if it's the left or right side path
): Path {
    return Path().apply {
        // Define starting and ending X positions for left and right sides
        val xStart = if (isLeft) -horizontalOffsetPx else outlineWidth
        val xEnd = if (isLeft) 0f else outlineWidth + horizontalOffsetPx

        moveTo(xStart, 0f)
        // Draw the rectangle path for the side
        lineTo(xEnd, 0f)
        lineTo(xEnd, outlineHeight + verticalBottomOffsetPx)
        lineTo(xStart, outlineHeight + verticalBottomOffsetPx)

        lineTo(xStart, 0f)
        close()
    }
}



