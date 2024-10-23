package com.skul.yuriy.composeplayground.util.shadowborder

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.shadowWithClipIntersect(
    elevation: Dp,
    shape: Shape = RectangleShape,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
): Modifier = this
    .drawWithCache {
        //  bottom shadow offset in Px based on elevation
        val bottomOffsetPx = elevation.toPx() * 2.2f
        // Adjust the size to extend the bottom by the bottom shadow offset
        val adjustedSize = Size(size.width, size.height + bottomOffsetPx)
        val outline = shape.createOutline(adjustedSize, layoutDirection, this)

        val path = Path().apply { addOutline(outline) }
        onDrawWithContent {
            clipPath(path, ClipOp.Intersect) {
                this@onDrawWithContent.drawContent()
            }
        }
    }
    .shadow(elevation, shape, clip, ambientColor, spotColor)