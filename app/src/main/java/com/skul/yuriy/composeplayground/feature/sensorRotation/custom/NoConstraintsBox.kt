package com.skul.yuriy.composeplayground.feature.sensorRotation.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

/**
 * A simple layout host that keeps the parent's own size, but measures its children with
 * unconstrained bounds. This is useful for experiments where the content should decide its own
 * size instead of inheriting the viewport constraints from regular Box measurement.
 */
@Composable
internal fun NoConstraintsBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val looseConstraints =
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
            )
        val placeables = measurables.map { it.measure(looseConstraints) }
        val layoutWidth =
            if (constraints.hasBoundedWidth) {
                constraints.maxWidth
            } else {
                placeables.maxOfOrNull { it.width } ?: constraints.minWidth
            }.coerceAtLeast(constraints.minWidth)
        val layoutHeight =
            if (constraints.hasBoundedHeight) {
                constraints.maxHeight
            } else {
                placeables.maxOfOrNull { it.height } ?: constraints.minHeight
            }.coerceAtLeast(constraints.minHeight)

        layout(layoutWidth, layoutHeight) {
            placeables.forEach { placeable ->
                val position = contentAlignment.align(
                    size = androidx.compose.ui.unit.IntSize(placeable.width, placeable.height),
                    space = androidx.compose.ui.unit.IntSize(layoutWidth, layoutHeight),
                    layoutDirection = layoutDirection,
                )
                placeable.place(position.x, position.y)
            }
        }
    }
}
