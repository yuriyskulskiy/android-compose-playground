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
    private var overflow: TextOverflow = TextOverflow.Clip,
    private var softWrap: Boolean = true,
    private var maxLines: Int = Int.MAX_VALUE,
    private var minLines: Int = 1,
) {
    var density: Density? = null

    var paragraph: Paragraph? = null
        private set

    var didOverflow: Boolean = false
        private set

    var layoutSize: IntSize = IntSize.Zero
        private set

    private var paragraphIntrinsics: ParagraphIntrinsics? = null
    private var intrinsicsLayoutDirection: LayoutDirection? = null
    private var prevConstraints: Constraints = Constraints.fixed(0, 0)
    private var cachedIntrinsicHeightInputWidth: Int = -1
    private var cachedIntrinsicHeight: Int = -1

    fun update(
        text: String,
        style: TextStyle,
        fontFamilyResolver: FontFamily.Resolver,
        overflow: TextOverflow,
        softWrap: Boolean,
        maxLines: Int,
        minLines: Int,
    ) {
        this.text = text
        this.style = style
        this.fontFamilyResolver = fontFamilyResolver
        this.overflow = overflow
        this.softWrap = softWrap
        this.maxLines = maxLines
        this.minLines = minLines
        markDirty()
    }

    fun layoutWithConstraints(constraints: Constraints, layoutDirection: LayoutDirection): Boolean {
        val finalConstraints = constraints
        if (!newLayoutWillBeDifferent(finalConstraints, layoutDirection)) {
            if (finalConstraints != prevConstraints) {
                val localParagraph = paragraph!!
                val layoutWidth = min(localParagraph.maxIntrinsicWidth, localParagraph.width)
                val localSize =
                    finalConstraints.constrain(
                        IntSize(layoutWidth.ceilToIntPx(), localParagraph.height.ceilToIntPx()),
                    )
                layoutSize = localSize
                didOverflow =
                    overflow != TextOverflow.Visible &&
                        (localSize.width < localParagraph.width ||
                            localSize.height < localParagraph.height)
                prevConstraints = finalConstraints
            }
            return false
        }

        paragraph =
            layoutText(finalConstraints, layoutDirection).also {
                prevConstraints = finalConstraints
                val localSize =
                    finalConstraints.constrain(
                        IntSize(it.width.ceilToIntPx(), it.height.ceilToIntPx()),
                    )
                layoutSize = localSize
                didOverflow =
                    overflow != TextOverflow.Visible &&
                        (localSize.width < it.width || localSize.height < it.height)
            }

        return true
    }

    fun intrinsicHeight(width: Int, layoutDirection: LayoutDirection): Int {
        if (width == cachedIntrinsicHeightInputWidth && cachedIntrinsicHeightInputWidth != -1) {
            return cachedIntrinsicHeight
        }

        val constraints = Constraints(0, width, 0, Constraints.Infinity)
        val result =
            layoutText(constraints, layoutDirection)
                .height
                .ceilToIntPx()
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

    private fun layoutText(constraints: Constraints, layoutDirection: LayoutDirection): Paragraph {
        val localParagraphIntrinsics = setLayoutDirection(layoutDirection)
        return Paragraph(
            paragraphIntrinsics = localParagraphIntrinsics,
            constraints =
                finalConstraints(
                    constraints = constraints,
                    softWrap = softWrap,
                    overflow = overflow,
                    maxIntrinsicWidth = localParagraphIntrinsics.maxIntrinsicWidth,
                ),
            maxLines = finalMaxLines(softWrap = softWrap, overflow = overflow, maxLinesIn = maxLines),
            overflow = overflow,
        )
    }

    private fun newLayoutWillBeDifferent(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
    ): Boolean {
        val localParagraph = paragraph ?: return true
        val localParagraphIntrinsics = paragraphIntrinsics ?: return true
        if (localParagraphIntrinsics.hasStaleResolvedFonts) return true
        if (layoutDirection != intrinsicsLayoutDirection) return true
        if (constraints == prevConstraints) return false
        if (constraints.maxWidth != prevConstraints.maxWidth) return true
        if (constraints.minWidth != prevConstraints.minWidth) return true
        if (constraints.maxHeight < localParagraph.height || localParagraph.didExceedMaxLines) return true
        return false
    }

    private fun markDirty() {
        paragraph = null
        paragraphIntrinsics = null
        intrinsicsLayoutDirection = null
        cachedIntrinsicHeightInputWidth = -1
        cachedIntrinsicHeight = -1
        prevConstraints = Constraints.fixed(0, 0)
        layoutSize = IntSize.Zero
        didOverflow = false
    }
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
