package com.skul.yuriy.composeplayground.feature.overflowText.investigate.prototype

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * Prototype that decomposes one long text into many single-line `Text` composables.
 * It first computes line slices from the available width, then renders each slice separately.
 */
@Composable
internal fun FlowTextPrototype(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color,
    floatingBoxSize: DpSize,
    floatingBoxGap: Dp,
    floatingBox: @Composable @UiComposable BoxScope.() -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = with(density) { maxWidth.roundToPx() }
        val floatingBoxWidthPx = with(density) { floatingBoxSize.width.roundToPx() }
        val floatingBoxHeightPx = with(density) { floatingBoxSize.height.roundToPx() }
        val floatingBoxGapPx = with(density) { floatingBoxGap.roundToPx() }

        val flowTextLayout = remember(
            text,
            style,
            maxWidthPx,
            floatingBoxWidthPx,
            floatingBoxHeightPx,
            floatingBoxGapPx,
            textMeasurer,
        ) {
            buildFlowTextLayout(
                text = text,
                style = style,
                textMeasurer = textMeasurer,
                containerWidthPx = maxWidthPx,
                floatingBoxWidthPx = floatingBoxWidthPx,
                floatingBoxHeightPx = floatingBoxHeightPx,
                floatingBoxGapPx = floatingBoxGapPx,
            )
        }

        val containerHeight = with(density) { flowTextLayout.totalHeightPx.toDp() }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight),
        ) {
            flowTextLayout.lines.forEach { line ->
                Text(
                    text = line.text,
                    color = color,
                    style = style,
                    modifier = Modifier
                        .widthIn(max = this@BoxWithConstraints.maxWidth)
                        .wrapContentHeight()
                        .align(Alignment.TopStart)
                        .offset { IntOffset(x = line.xPx, y = line.yPx) }
                        .border(width = 1.dp, color = Color.Black),
                )
            }

            Box(
                modifier = Modifier
                    .size(floatingBoxSize)
                    .align(Alignment.TopEnd),
                content = floatingBox,
            )
        }
    }
}

private fun buildFlowTextLayout(
    text: String,
    style: TextStyle,
    textMeasurer: TextMeasurer,
    containerWidthPx: Int,
    floatingBoxWidthPx: Int,
    floatingBoxHeightPx: Int,
    floatingBoxGapPx: Int,
): FlowTextLayout {
    if (containerWidthPx <= 0) {
        return FlowTextLayout(emptyList(), totalHeightPx = floatingBoxHeightPx)
    }

    val defaultLineHeightPx = textMeasurer.measure(
        text = "Hg",
        style = style,
        maxLines = 1,
        softWrap = false,
    ).size.height

    val lines = buildList {
        var currentY = 0

        text.split('\n').forEach { paragraph: String ->
            if (paragraph.isEmpty()) {
                currentY += defaultLineHeightPx
                return@forEach
            }

            var remaining = paragraph
            while (remaining.isNotEmpty()) {
                val availableWidthPx = max(
                    1,
                    if (currentY < floatingBoxHeightPx) {
                        containerWidthPx - floatingBoxWidthPx - floatingBoxGapPx
                    } else {
                        containerWidthPx
                    },
                )

                val lineResult = textMeasurer.measure(
                    text = remaining,
                    style = style,
                    maxLines = 1,
                    softWrap = true,
                    constraints = Constraints(maxWidth = availableWidthPx),
                )

                val consumedCount = max(
                    1,
                    lineResult.getLineEnd(lineIndex = 0, visibleEnd = false),
                )
                val consumedText = remaining.take(consumedCount)

                add(
                    FlowTextLine(
                        text = consumedText.trimEnd(),
                        xPx = 0,
                        yPx = currentY,
                    ),
                )

                currentY += lineResult.size.height
                remaining = remaining.drop(consumedCount)
            }
        }
    }

    val textHeightPx = lines.lastOrNull()?.let { lastLine ->
        lastLine.yPx + defaultLineHeightPx
    } ?: 0

    return FlowTextLayout(
        lines = lines,
        totalHeightPx = max(textHeightPx, floatingBoxHeightPx),
    )
}

/** Stores the computed lines and the final container height for the prototype. */
private data class FlowTextLayout(
    val lines: List<FlowTextLine>,
    val totalHeightPx: Int,
)

/** Represents one rendered line fragment with its top-left position. */
private data class FlowTextLine(
    val text: String,
    val xPx: Int,
    val yPx: Int,
)
