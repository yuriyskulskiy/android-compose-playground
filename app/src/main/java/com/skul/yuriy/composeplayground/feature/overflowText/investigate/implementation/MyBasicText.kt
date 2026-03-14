package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Clip
import androidx.compose.ui.unit.Constraints

/**
 * Step-by-step copy of the string-based `androidx.compose.foundation.text.BasicText`.
 * This layer is intentionally rebuilt inside the project so deeper text behavior can be changed
 * later without going through the library composable entry point.
 */
@Composable
internal fun MyBasicText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
) {
    validateMyMinMaxLines(minLines = minLines, maxLines = maxLines)
    require(autoSize == null) { "MyBasicText does not support autoSize yet." }

    val fontFamilyResolver = LocalFontFamilyResolver.current
    // The original BasicText also branches into a more general path for selection, onTextLayout,
    // and autoSize. This step keeps only the simple string-based path.
    val finalModifier =
        modifier.then(
            MyTextStringSimpleElement(
                text = text,
                style = style,
                fontFamilyResolver = fontFamilyResolver,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                color = color,
                onTextLayout = onTextLayout,
            ),
        )

    Layout(
        modifier = finalModifier,
        measurePolicy = MyEmptyMeasurePolicy,
    )
}

private fun validateMyMinMaxLines(minLines: Int, maxLines: Int) {
    require(minLines > 0 && maxLines > 0) {
        "both minLines $minLines and maxLines $maxLines must be greater than zero"
    }
    require(minLines <= maxLines) {
        "minLines $minLines must be less than or equal to maxLines $maxLines"
    }
}

private object MyEmptyMeasurePolicy : MeasurePolicy {
    private val placementBlock: Placeable.PlacementScope.() -> Unit = {}

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        return layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
            placementBlock = placementBlock,
        )
    }
}
