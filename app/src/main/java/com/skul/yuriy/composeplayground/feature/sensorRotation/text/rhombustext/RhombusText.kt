package com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombustext

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Clip
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalDensity
import com.skul.yuriy.composeplayground.feature.sensorRotation.scroll.rememberRotationAwareFlingBehavior

/**
 * Project-owned text entry point for rhombus/parallelogram-like layout.
 *
 * Unlike a regular paragraph, this composable is designed for containers whose text lines keep the
 * same width but shift horizontally from one line to the next.
 */
@Composable
internal fun RhombusText(
    text: String,
    angleDegrees: Float,
    config: RhombusTextLayoutConfig,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
) {
    validateRhombusMinMaxLines(minLines = minLines, maxLines = maxLines)
    require(autoSize == null) { "RhombusText does not support autoSize yet." }
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    val flingBehavior = rememberRotationAwareFlingBehavior(angleDegrees)
    val finalConfig = config.copy(
        scrollOffset = with(density) { scrollState.value.toDp() }
    )

    val finalModifier =
        modifier
            .verticalScroll(
                state = scrollState,
                flingBehavior = flingBehavior,
            )
            .then(
            MyRhombusTextStringSimpleElement(
                text = text,
                style = style,
                fontFamilyResolver = fontFamilyResolver,
                config = finalConfig,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                color = color,
                onTextLayout = onTextLayout,
            )
        )

    Layout(
        modifier = finalModifier,
        measurePolicy = RhombusEmptyMeasurePolicy,
    )
}

private fun validateRhombusMinMaxLines(minLines: Int, maxLines: Int) {
    require(minLines > 0 && maxLines > 0) {
        "both minLines $minLines and maxLines $maxLines must be greater than zero"
    }
    require(minLines <= maxLines) {
        "minLines $minLines must be less than or equal to maxLines $maxLines"
    }
}

private object RhombusEmptyMeasurePolicy : MeasurePolicy {
    private val placementBlock: Placeable.PlacementScope.() -> Unit = {}

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult = layout(
        width = constraints.maxWidth,
        height = constraints.maxHeight,
        placementBlock = placementBlock,
    )
}
