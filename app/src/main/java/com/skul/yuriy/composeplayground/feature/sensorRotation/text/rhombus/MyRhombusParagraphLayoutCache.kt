package com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombus

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.MultiParagraphIntrinsics
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.resolveDefaults
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrain
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Project-owned paragraph cache for rhombus text.
 *
 * Current implementation uses one paragraph fragment per visual line and shifts each next line by
 * a delta derived from line height. This matches a parallelogram-like text area where line width
 * stays constant while the start position drifts left or right.
 */
internal class MyRhombusParagraphLayoutCache(
    private var text: String,
    private var style: TextStyle,
    private var fontFamilyResolver: FontFamily.Resolver,
    private var config: RhombusTextLayoutConfig,
    private var overflow: TextOverflow = TextOverflow.Clip,
    private var softWrap: Boolean = true,
    private var maxLines: Int = Int.MAX_VALUE,
    private var minLines: Int = 1,
) {
    var density: Density? = null

    var fragments: List<RhombusTextFragment> = emptyList()
        private set

    var didOverflow: Boolean = false
        private set

    var layoutSize: IntSize = IntSize.Zero
        private set

    private var paragraphIntrinsics: ParagraphIntrinsics? = null
    private var intrinsicsLayoutDirection: LayoutDirection? = null
    private var prevConstraints: Constraints = Constraints.fixed(0, 0)
    private var previousConfig: RhombusTextLayoutConfig? = null

    fun update(
        text: String,
        style: TextStyle,
        fontFamilyResolver: FontFamily.Resolver,
        config: RhombusTextLayoutConfig,
        overflow: TextOverflow,
        softWrap: Boolean,
        maxLines: Int,
        minLines: Int,
    ) {
        this.text = text
        this.style = style
        this.fontFamilyResolver = fontFamilyResolver
        this.config = config
        this.overflow = overflow
        this.softWrap = softWrap
        this.maxLines = maxLines
        this.minLines = minLines
        markDirty()
    }

    fun layoutWithConstraints(constraints: Constraints, layoutDirection: LayoutDirection): Boolean {
        if (!newLayoutWillBeDifferent(constraints, layoutDirection)) return false

        fragments = layoutFragments(constraints, layoutDirection)
        prevConstraints = constraints
        previousConfig = config
        val naturalSize = calculateNaturalSize(fragments)
        layoutSize = constraints.constrain(naturalSize)
        didOverflow =
            overflow != TextOverflow.Visible &&
                (layoutSize.width < naturalSize.width || layoutSize.height < naturalSize.height)
        return true
    }

    fun intrinsicHeight(width: Int, layoutDirection: LayoutDirection): Int {
        val measuredConstraints = Constraints(0, width, 0, Constraints.Infinity)
        return calculateNaturalSize(layoutFragments(measuredConstraints, layoutDirection)).height
    }

    fun minIntrinsicWidth(layoutDirection: LayoutDirection): Int {
        val lineWidthPx = checkNotNull(density) { "Density must be set before layout." }.run {
            val edgeInsetPx = config.edgeInset.roundToPx()
            max(1, config.lineWidth.roundToPx() - edgeInsetPx * 2)
        }
        return lineWidthPx
    }

    fun maxIntrinsicWidth(layoutDirection: LayoutDirection): Int = minIntrinsicWidth(layoutDirection)

    fun createTextLayoutResult(): TextLayoutResult {
        val localDensity = checkNotNull(density) { "Density must be set before layout result creation." }
        val localLayoutDirection =
            checkNotNull(intrinsicsLayoutDirection) { "LayoutDirection must be known before layout result creation." }
        val annotatedString = AnnotatedString(text)
        val horizontalPaddingPx = localDensity.run { config.horizontalPadding.roundToPx() }
        val edgeInsetPx = localDensity.run { config.edgeInset.roundToPx() }
        val effectiveLineWidthPx =
            max(1, localDensity.run { config.lineWidth.roundToPx() } - horizontalPaddingPx * 2 - edgeInsetPx * 2)
        val resultConstraints = Constraints(0, effectiveLineWidthPx, 0, Constraints.Infinity)

        return TextLayoutResult(
            layoutInput =
                TextLayoutInput(
                    text = annotatedString,
                    style = style,
                    placeholders = emptyList(),
                    maxLines = maxLines,
                    softWrap = softWrap,
                    overflow = overflow,
                    density = localDensity,
                    layoutDirection = localLayoutDirection,
                    fontFamilyResolver = fontFamilyResolver,
                    constraints = resultConstraints,
                ),
            multiParagraph =
                MultiParagraph(
                    intrinsics =
                        MultiParagraphIntrinsics(
                            annotatedString = annotatedString,
                            style = style,
                            placeholders = emptyList(),
                            density = localDensity,
                            fontFamilyResolver = fontFamilyResolver,
                        ),
                    constraints = resultConstraints,
                    maxLines = finalMaxLines(softWrap, overflow, maxLines),
                    overflow = overflow,
                ),
            size = layoutSize,
        )
    }

    private fun layoutFragments(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
    ): List<RhombusTextFragment> {
        intrinsicsLayoutDirection = layoutDirection
        if (
            !softWrap ||
            !constraints.hasBoundedWidth ||
            constraints.maxWidth == Constraints.Infinity
        ) {
            return listOf(
                RhombusTextFragment(
                    paragraph = layoutText(
                        text = text,
                        constraints = constraints,
                        layoutDirection = layoutDirection,
                    ),
                    offsetX = 0,
                    offsetY = 0,
                )
            )
        }

        val localDensity = checkNotNull(density) { "Density must be set before layout." }
        val horizontalPaddingPx = localDensity.run { config.horizontalPadding.roundToPx() }
        val verticalPaddingPx = localDensity.run { config.verticalPadding.roundToPx() }
        val edgeInsetPx = localDensity.run { config.edgeInset.roundToPx() }
        val lineWidthPx =
            max(
                1,
                minOf(
                    localDensity.run { config.lineWidth.roundToPx() } - horizontalPaddingPx * 2 - edgeInsetPx * 2,
                    constraints.maxWidth
                )
            )
        val firstLineOffsetPx = localDensity.run { config.firstLineOffset.roundToPx() }
        var remainingText = text
        var remainingLines = finalMaxLines(softWrap = softWrap, overflow = overflow, maxLinesIn = maxLines)
        var currentOffsetY = verticalPaddingPx
        val fragments = mutableListOf<RhombusTextFragment>()

        while (remainingText.isNotEmpty() && remainingLines > 0) {
            val paragraph =
                layoutText(
                    text = remainingText,
                    constraints = Constraints(0, lineWidthPx, 0, Constraints.Infinity),
                    layoutDirection = layoutDirection,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )
            if (paragraph.lineCount <= 0) break

            val consumedCharacterCount =
                paragraph.getLineEnd(0, visibleEnd = false)
                    .coerceIn(0, remainingText.length)
                    .coerceAtLeast(1)

            val currentOffsetX =
                firstLineOffsetPx +
                    horizontalPaddingPx +
                    edgeInsetPx +
                    (currentOffsetY * config.horizontalShiftPerHeight).roundToInt()

            fragments +=
                RhombusTextFragment(
                    paragraph = paragraph,
                    offsetX = currentOffsetX,
                    offsetY = currentOffsetY,
                )

            remainingText = remainingText.drop(consumedCharacterCount)
            val lineHeight = paragraph.getLineBottom(0).ceilToIntPx()
            currentOffsetY += lineHeight
            remainingLines--
        }

        if (fragments.isEmpty()) {
            return listOf(
                RhombusTextFragment(
                    paragraph = layoutText(
                        text = text,
                        constraints = constraints,
                        layoutDirection = layoutDirection,
                    ),
                    offsetX = 0,
                    offsetY = 0,
                )
            )
        }

        return fragments
    }

    private fun layoutText(
        text: String,
        constraints: Constraints,
        layoutDirection: LayoutDirection,
        maxLines: Int = this.maxLines,
        overflow: TextOverflow = this.overflow,
    ): Paragraph {
        val resolvedStyle = resolveDefaults(style, layoutDirection)
        val paragraphIntrinsics =
            ParagraphIntrinsics(
                text = text,
                style = resolvedStyle,
                annotations = listOf(),
                density = checkNotNull(density) { "Density must be set before layout." },
                fontFamilyResolver = fontFamilyResolver,
                placeholders = listOf(),
            )
        this.paragraphIntrinsics = paragraphIntrinsics
        return Paragraph(
            paragraphIntrinsics = paragraphIntrinsics,
            constraints =
                finalConstraints(
                    constraints = constraints,
                    softWrap = softWrap,
                    overflow = overflow,
                    maxIntrinsicWidth = paragraphIntrinsics.maxIntrinsicWidth,
                ),
            maxLines = finalMaxLines(softWrap = softWrap, overflow = overflow, maxLinesIn = maxLines),
            overflow = overflow,
        )
    }

    private fun newLayoutWillBeDifferent(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
    ): Boolean {
        if (fragments.isEmpty()) return true
        if (layoutDirection != intrinsicsLayoutDirection) return true
        if (config != previousConfig) return true
        if (constraints != prevConstraints) return true
        val localIntrinsics = paragraphIntrinsics ?: return true
        return localIntrinsics.hasStaleResolvedFonts
    }

    private fun markDirty() {
        fragments = emptyList()
        paragraphIntrinsics = null
        intrinsicsLayoutDirection = null
        prevConstraints = Constraints.fixed(0, 0)
        previousConfig = null
        layoutSize = IntSize.Zero
        didOverflow = false
    }
}

internal data class RhombusTextFragment(
    val paragraph: Paragraph,
    val offsetX: Int,
    val offsetY: Int,
)

private fun calculateNaturalSize(fragments: List<RhombusTextFragment>): IntSize {
    if (fragments.isEmpty()) return IntSize.Zero
    val width =
        fragments.maxOf { fragment ->
            fragment.offsetX + fragment.paragraph.width.ceilToIntPx()
        }
    val height =
        fragments.maxOf { fragment ->
            fragment.offsetY + fragment.paragraph.height.ceilToIntPx()
        }
    return IntSize(width = width, height = height)
}

private fun finalConstraints(
    constraints: Constraints,
    softWrap: Boolean,
    overflow: TextOverflow,
    maxIntrinsicWidth: Float,
): Constraints =
    Constraints.fitPrioritizingWidth(
        minWidth = 0,
        maxWidth = finalMaxWidth(constraints, softWrap, overflow, maxIntrinsicWidth),
        minHeight = 0,
        maxHeight = constraints.maxHeight,
    )

private fun finalMaxWidth(
    constraints: Constraints,
    softWrap: Boolean,
    overflow: TextOverflow,
    maxIntrinsicWidth: Float,
): Int {
    val widthMatters = softWrap || overflow.isEllipsis
    val maxWidth =
        if (widthMatters && constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            Constraints.Infinity
        }

    return if (constraints.minWidth == maxWidth) {
        maxWidth
    } else {
        maxIntrinsicWidth.ceilToIntPx().coerceIn(constraints.minWidth, maxWidth)
    }
}

private fun finalMaxLines(
    softWrap: Boolean,
    overflow: TextOverflow,
    maxLinesIn: Int,
): Int {
    val overwriteMaxLines = !softWrap && overflow.isEllipsis
    return if (overwriteMaxLines) 1 else max(maxLinesIn, 1)
}

private val TextOverflow.isEllipsis: Boolean
    get() =
        this == TextOverflow.Ellipsis ||
            this == TextOverflow.StartEllipsis ||
            this == TextOverflow.MiddleEllipsis

private fun Float.ceilToIntPx(): Int = ceil(this).toInt()
