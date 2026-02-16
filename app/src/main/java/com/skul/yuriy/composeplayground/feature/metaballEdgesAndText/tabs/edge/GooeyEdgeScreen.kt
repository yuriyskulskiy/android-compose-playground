package com.skul.yuriy.composeplayground.feature.metaballEdgesAndText.tabs.edge

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.applyRenderEffect
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold50PercentEffect
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun GooeyEdgeScreen(
    modifier: Modifier = Modifier,
) {

    //region Visual tuning params for metaball effect
    val blurRadius = 40.dp
    val circleRadius: Dp = 56.dp
    val gooeyBottomEdgeHeight = 56.dp
    val gooeyTopEdgeHeight = 156.dp
    val edgeMaxAlpha = 0.5f
    val alphaThreshold = alphaThreshold50PercentEffect
    // Keep alphaThreshold cutoff >= edgeMaxAlpha,
    // otherwise top/bottom edge gradients can leak through after filtering.
    //endregion



    val radiusPx = with(LocalDensity.current) { circleRadius.toPx() }
    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    // Must be remembered: a new DecayAnimationSpec per recomposition would recreate
    // DragFlingController and reset center to Offset.Zero during animated layout changes.
    val decay = remember { exponentialDecay<Offset>(frictionMultiplier = 3f) }
    val controller = rememberDragFlingController(
        radiusPx = { radiusPx },
        containerSize = { Size(containerWidth, containerHeight) },
        overflowMultiplier = 3f,
        velocityScale = 1f,
        scope = scope,
        decay = decay,
    )

    //places circle in center
    LaunchedEffect(containerWidth, containerHeight) {
        controller.centerIfUnset()
    }

    //the root screen  box wrapper for circle and gradient edges - the point to apply alpha filter
    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                containerWidth = size.width.toFloat()
                containerHeight = size.height.toFloat()
            }
            .applyRenderEffect(alphaThreshold)
            .pointerInput(radiusPx, containerWidth, containerHeight) {
                controller.handleDrag(this)
            },
        contentAlignment = Alignment.TopStart,
    ) {

        //gooey  vertical edge edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(gooeyTopEdgeHeight)
                .align(alignment = Alignment.TopStart)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = edgeMaxAlpha),
                            Color.Black.copy(alpha = 0.0f)
                        )
                    )
                )
        )

        //gooey  vertical bottom edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(gooeyBottomEdgeHeight)
                .align(alignment = Alignment.BottomEnd)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Red.copy(alpha = 0.0f),
                            Color.Red.copy(alpha = edgeMaxAlpha)
                        )
                    )
                )
        )


        //draggable and flingable  circle
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (controller.center.x - radiusPx).roundToInt(),
                        (controller.center.y - radiusPx).roundToInt()
                    )
                }
                .size(circleRadius * 2)
                .blur(radius = blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(Color.Black, shape = CircleShape)
        )
    }
}
