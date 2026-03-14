package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

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
import kotlin.math.min

/**
 * Step-by-step copy of `androidx.compose.foundation.text.modifiers.ParagraphLayoutCache`.
 * This cache is project-owned so text layout rules can later be changed for flow-around behavior.
 */
internal class MyParagraphLayoutCache(
    private var text: String,
    private var style: TextStyle,
    private var fontFamilyResolver: FontFamily.Resolver,
    private var config: FloatingBoxConfig? = null,
    private var overflow: TextOverflow = TextOverflow.Clip,
    private var softWrap: Boolean = true,
    private var maxLines: Int = Int.MAX_VALUE,
    private var minLines: Int = 1,
) {
    var density: Density? = null

    var fragments: List<FlowTextFragment> = emptyList()
        private set

    var didOverflow: Boolean = false
        private set

    var layoutSize: IntSize = IntSize.Zero
        private set

    private var paragraphIntrinsics: ParagraphIntrinsics? = null
    private var intrinsicsLayoutDirection: LayoutDirection? = null
    private var previousConfig: FloatingBoxConfig? = null
    private var prevConstraints: Constraints = Constraints.fixed(0, 0)
    private var cachedIntrinsicHeightInputWidth: Int = -1
    private var cachedIntrinsicHeight: Int = -1

    fun update(
        text: String,
        style: TextStyle,
        fontFamilyResolver: FontFamily.Resolver,
        config: FloatingBoxConfig?,
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
        // TODO MODIFY: this is only the first fragment-based path for the top-end floating box.
        val finalConstraints = constraints
        if (!newLayoutWillBeDifferent(finalConstraints, layoutDirection)) {
            if (finalConstraints != prevConstraints) {
                val naturalSize = calculateNaturalSize(fragments)
                layoutSize = finalConstraints.constrain(naturalSize)
                didOverflow =
                    overflow != TextOverflow.Visible &&
                        (layoutSize.width < naturalSize.width ||
                            layoutSize.height < naturalSize.height ||
                            fragments.any { it.paragraph.didExceedMaxLines })
                prevConstraints = finalConstraints
            }
            return false
        }

        fragments = layoutFragments(finalConstraints, layoutDirection)
        prevConstraints = finalConstraints
        previousConfig = config
        val naturalSize = calculateNaturalSize(fragments)
        layoutSize = finalConstraints.constrain(naturalSize)
        didOverflow =
            overflow != TextOverflow.Visible &&
                (layoutSize.width < naturalSize.width ||
                    layoutSize.height < naturalSize.height ||
                    fragments.any { it.paragraph.didExceedMaxLines })

        return true
    }

    fun intrinsicHeight(width: Int, layoutDirection: LayoutDirection): Int {
        // TODO MODIFY: this now uses fragments, but still only for the first simplified flow path.
        if (width == cachedIntrinsicHeightInputWidth && cachedIntrinsicHeightInputWidth != -1) {
            return cachedIntrinsicHeight
        }

        val constraints = Constraints(0, width, 0, Constraints.Infinity)
        val result =
            calculateNaturalSize(layoutFragments(constraints, layoutDirection))
                .height
                .coerceAtLeast(constraints.minHeight)

        cachedIntrinsicHeightInputWidth = width
        cachedIntrinsicHeight = result
        return result
    }

    fun minIntrinsicWidth(layoutDirection: LayoutDirection): Int {
        return setLayoutDirection(layoutDirection).minIntrinsicWidth.ceilToIntPx()
    }

    fun maxIntrinsicWidth(layoutDirection: LayoutDirection): Int {
        return setLayoutDirection(layoutDirection).maxIntrinsicWidth.ceilToIntPx()
    }

    fun createTextLayoutResult(): TextLayoutResult {
        // TODO MODIFY: this assumes one MultiParagraph result instead of flow fragments.
        val localDensity = checkNotNull(density) { "Density must be set before layout result creation." }
        val localLayoutDirection =
            checkNotNull(intrinsicsLayoutDirection) { "LayoutDirection must be known before layout result creation." }
        val annotatedString = AnnotatedString(text)
        val finalConstraints = prevConstraints.copyMaxDimensions()

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
                    constraints = finalConstraints,
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
                    constraints = finalConstraints,
                    maxLines = finalMaxLines(softWrap, overflow, maxLines),
                    overflow = overflow,
                ),
            size = layoutSize,
        )
    }

    private fun setLayoutDirection(layoutDirection: LayoutDirection): ParagraphIntrinsics {
        val localIntrinsics = paragraphIntrinsics
        val intrinsics =
            if (
                localIntrinsics == null ||
                    layoutDirection != intrinsicsLayoutDirection ||
                    localIntrinsics.hasStaleResolvedFonts
            ) {
                intrinsicsLayoutDirection = layoutDirection
                ParagraphIntrinsics(
                    text = text,
                    style = resolveDefaults(style, layoutDirection),
                    annotations = listOf(),
                    density = checkNotNull(density) { "Density must be set before layout." },
                    fontFamilyResolver = fontFamilyResolver,
                    placeholders = listOf(),
                )
            } else {
                localIntrinsics
            }
        paragraphIntrinsics = intrinsics
        return intrinsics
    }

    private fun layoutFragments(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
    ): List<FlowTextFragment> {
        intrinsicsLayoutDirection = layoutDirection
        val localConfig = config
        if (
            localConfig == null ||
                !softWrap ||
                !constraints.hasBoundedWidth ||
                constraints.maxWidth == Constraints.Infinity
        ) {
            return listOf(
                FlowTextFragment(
                    paragraph = layoutText(text = text, constraints = constraints, layoutDirection = layoutDirection),
                    offsetX = 0,
                    offsetY = 0,
                ),
            )
        }

        val localDensity = checkNotNull(density) { "Density must be set before layout." }
        val topFragmentWidth =
            max(
                1,
                constraints.maxWidth - localDensity.run { localConfig.width.roundToPx() + localConfig.gap.roundToPx() },
            )
        val boxTop = max(0, localDensity.run { localConfig.topOffset.roundToPx() })
        val boxHeight = max(0, localDensity.run { localConfig.height.roundToPx() })
        val boxBottom = boxTop + boxHeight
        val fullWidth = max(1, constraints.maxWidth)
        val finalMaxLines = finalMaxLines(softWrap = softWrap, overflow = overflow, maxLinesIn = maxLines)

        if (boxHeight == 0 || topFragmentWidth >= fullWidth || text.isEmpty()) {
            return listOf(
                FlowTextFragment(
                    paragraph = layoutText(text = text, constraints = constraints, layoutDirection = layoutDirection),
                    offsetX = 0,
                    offsetY = 0,
                ),
            )
        }

        val topFullWidthMeasurement = layoutText(
            text = text,
            constraints = Constraints(0, fullWidth, 0, Constraints.Infinity),
            layoutDirection = layoutDirection,
            maxLines = finalMaxLines,
            overflow = TextOverflow.Clip,
        )
        val topConsumedCharacterCount =
            consumedCharacterCountBeforeHeight(
                paragraph = topFullWidthMeasurement,
                text = text,
                availableHeight = boxTop,
            )
        if (topConsumedCharacterCount >= text.length) {
            return listOf(
                FlowTextFragment(
                    paragraph = topFullWidthMeasurement,
                    offsetX = 0,
                    offsetY = 0,
                ),
            )
        }

        val fragments = mutableListOf<FlowTextFragment>()
        var consumedCharacterCount = 0
        var consumedLineCount = 0
        var currentYOffset = 0

        if (topConsumedCharacterCount > 0) {
            val topText = text.substring(0, topConsumedCharacterCount)
            val topParagraph = layoutText(
                text = topText,
                constraints = Constraints(0, fullWidth, 0, Constraints.Infinity),
                layoutDirection = layoutDirection,
                maxLines = finalMaxLines,
                overflow = TextOverflow.Clip,
            )
            fragments +=
                FlowTextFragment(
                    paragraph = topParagraph,
                    offsetX = 0,
                    offsetY = 0,
                )
            consumedCharacterCount = topConsumedCharacterCount
            consumedLineCount = topParagraph.lineCount
            currentYOffset = topParagraph.height.ceilToIntPx()
        }

        val remainingTextAfterTop = text.substring(consumedCharacterCount)
        val remainingLinesAfterTop = (finalMaxLines - consumedLineCount).coerceAtLeast(0)
        if (remainingTextAfterTop.isEmpty() || remainingLinesAfterTop == 0) {
            return fragments.ifEmpty {
                listOf(
                    FlowTextFragment(
                        paragraph = layoutText(text = text, constraints = constraints, layoutDirection = layoutDirection),
                        offsetX = 0,
                        offsetY = 0,
                    ),
                )
            }
        }

        val narrowMeasurement = layoutText(
            text = remainingTextAfterTop,
            constraints = Constraints(0, topFragmentWidth, 0, Constraints.Infinity),
            layoutDirection = layoutDirection,
            maxLines = remainingLinesAfterTop,
            overflow = TextOverflow.Clip,
        )
        val relativeBoxTop = max(0, boxTop - currentYOffset)
        val relativeBoxBottom = max(relativeBoxTop, boxBottom - currentYOffset)
        val narrowConsumedCharacterCount =
            consumedCharacterCountIntersectingHeightRange(
                paragraph = narrowMeasurement,
                text = remainingTextAfterTop,
                startHeight = relativeBoxTop,
                endHeight = relativeBoxBottom,
            )

        if (narrowConsumedCharacterCount > 0) {
            val narrowText = remainingTextAfterTop.substring(0, narrowConsumedCharacterCount)
            val narrowParagraph = layoutText(
                text = narrowText,
                constraints = Constraints(0, topFragmentWidth, 0, Constraints.Infinity),
                layoutDirection = layoutDirection,
                maxLines = remainingLinesAfterTop,
                overflow = TextOverflow.Clip,
            )
            fragments +=
                FlowTextFragment(
                    paragraph = narrowParagraph,
                    offsetX = 0,
                    offsetY = currentYOffset,
                )
            consumedCharacterCount += narrowConsumedCharacterCount
            consumedLineCount += narrowParagraph.lineCount
            currentYOffset += narrowParagraph.height.ceilToIntPx()
        }

        val remainingTextAfterNarrow = text.substring(consumedCharacterCount)
        val remainingLinesAfterNarrow = (finalMaxLines - consumedLineCount).coerceAtLeast(0)
        if (remainingTextAfterNarrow.isEmpty() || remainingLinesAfterNarrow == 0) {
            return fragments.ifEmpty {
                listOf(
                    FlowTextFragment(
                        paragraph = layoutText(text = text, constraints = constraints, layoutDirection = layoutDirection),
                        offsetX = 0,
                        offsetY = 0,
                    ),
                )
            }
        }

        val bottomParagraph = layoutText(
            text = remainingTextAfterNarrow,
            constraints = Constraints(0, fullWidth, 0, constraints.maxHeight),
            layoutDirection = layoutDirection,
            maxLines = remainingLinesAfterNarrow,
            overflow = overflow,
        )
        fragments +=
            FlowTextFragment(
                paragraph = bottomParagraph,
                offsetX = 0,
                offsetY = currentYOffset,
            )

        return fragments.ifEmpty {
            listOf(
                FlowTextFragment(
                    paragraph = layoutText(text = text, constraints = constraints, layoutDirection = layoutDirection),
                    offsetX = 0,
                    offsetY = 0,
                ),
            )
        }
    }

    private fun layoutText(
        text: String,
        constraints: Constraints,
        layoutDirection: LayoutDirection,
        maxLines: Int = this.maxLines,
        overflow: TextOverflow = this.overflow,
    ): Paragraph {
        // TODO MODIFY: this helper still creates one rectangular Paragraph per fragment.
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
        // TODO MODIFY: cache invalidation will also need to depend on floating box geometry.
        if (fragments.isEmpty()) return true
        val localParagraphIntrinsics = paragraphIntrinsics ?: return true
        if (localParagraphIntrinsics.hasStaleResolvedFonts) return true
        if (layoutDirection != intrinsicsLayoutDirection) return true
        if (config != previousConfig) return true
        if (constraints == prevConstraints) return false
        if (constraints.maxWidth != prevConstraints.maxWidth) return true
        if (constraints.minWidth != prevConstraints.minWidth) return true
        if (constraints.maxHeight < layoutSize.height || fragments.any { it.paragraph.didExceedMaxLines }) {
            return true
        }
        return false
    }

    private fun markDirty() {
        fragments = emptyList()
        paragraphIntrinsics = null
        intrinsicsLayoutDirection = null
        previousConfig = null
        cachedIntrinsicHeightInputWidth = -1
        cachedIntrinsicHeight = -1
        prevConstraints = Constraints.fixed(0, 0)
        layoutSize = IntSize.Zero
        didOverflow = false
    }
}

internal data class FlowTextFragment(
    val paragraph: Paragraph,
    val offsetX: Int,
    val offsetY: Int,
)

private fun calculateNaturalSize(fragments: List<FlowTextFragment>): IntSize {
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

private fun consumedCharacterCountBeforeHeight(
    paragraph: Paragraph,
    text: String,
    availableHeight: Int,
): Int {
    if (text.isEmpty()) return 0
    if (availableHeight <= 0) return 0

    var lastVisibleLineIndex = -1
    for (lineIndex in 0 until paragraph.lineCount) {
        if (paragraph.getLineBottom(lineIndex) <= availableHeight) {
            lastVisibleLineIndex = lineIndex
        } else {
            break
        }
    }

    if (lastVisibleLineIndex < 0) return 0
    return paragraph.getLineEnd(lastVisibleLineIndex, visibleEnd = false).coerceIn(0, text.length)
}

private fun consumedCharacterCountIntersectingHeightRange(
    paragraph: Paragraph,
    text: String,
    startHeight: Int,
    endHeight: Int,
): Int {
    if (text.isEmpty()) return 0
    if (endHeight <= startHeight) return 0

    var lastIntersectingLineIndex = -1
    for (lineIndex in 0 until paragraph.lineCount) {
        val lineTop = paragraph.getLineTop(lineIndex).ceilToIntPx()
        val lineBottom = paragraph.getLineBottom(lineIndex).ceilToIntPx()
        val intersectsBox = lineBottom > startHeight && lineTop < endHeight
        if (intersectsBox) {
            lastIntersectingLineIndex = lineIndex
        } else if (lastIntersectingLineIndex >= 0 && lineTop >= endHeight) {
            break
        }
    }

    if (lastIntersectingLineIndex < 0) return 0
    return paragraph.getLineEnd(lastIntersectingLineIndex, visibleEnd = false).coerceIn(0, text.length)
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
