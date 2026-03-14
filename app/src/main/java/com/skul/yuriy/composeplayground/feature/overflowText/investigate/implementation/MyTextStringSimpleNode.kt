package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.invalidateLayer
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Constraints.Companion.fitPrioritizingWidth
import androidx.compose.ui.util.fastRoundToInt

/**
 * Step-by-step copy of the string-based `androidx.compose.foundation.text.modifiers.TextStringSimpleNode`.
 * This node keeps the same responsibility boundary as the library node while we move text logic
 * into project-owned code.
 */
internal class MyTextStringSimpleNode(
    private var text: String,
    private var style: TextStyle,
    private var fontFamilyResolver: FontFamily.Resolver,
    private var overflow: TextOverflow = TextOverflow.Clip,
    private var softWrap: Boolean = true,
    private var maxLines: Int = Int.MAX_VALUE,
    private var minLines: Int = 1,
    private var overrideColor: Color = Color.Unspecified,
    private var onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) : Modifier.Node(), LayoutModifierNode, DrawModifierNode {
    // This node mirrors the simple string text path only. The original implementation has extra
    // selection and semantics integration that is intentionally not copied yet.

    @Suppress("PrimitiveInCollection")
    private var baselineCache: MutableMap<AlignmentLine, Int>? = null

    private val layoutCache: MyParagraphLayoutCache by lazy {
        MyParagraphLayoutCache(
            text = text,
            style = style,
            fontFamilyResolver = fontFamilyResolver,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
        )
    }

    fun update(
        text: String,
        style: TextStyle,
        fontFamilyResolver: FontFamily.Resolver,
        overflow: TextOverflow,
        softWrap: Boolean,
        maxLines: Int,
        minLines: Int,
        overrideColor: Color,
        onTextLayout: ((TextLayoutResult) -> Unit)?,
    ) {
        val textChanged = this.text != text
        val layoutChanged =
            this.style != style ||
                this.fontFamilyResolver != fontFamilyResolver ||
                this.overflow != overflow ||
                this.softWrap != softWrap ||
                this.maxLines != maxLines ||
                this.minLines != minLines
        val drawChanged =
            this.overrideColor != overrideColor || this.style != style || this.onTextLayout !== onTextLayout

        this.text = text
        this.style = style
        this.fontFamilyResolver = fontFamilyResolver
        this.overflow = overflow
        this.softWrap = softWrap
        this.maxLines = maxLines
        this.minLines = minLines
        this.overrideColor = overrideColor
        this.onTextLayout = onTextLayout

        if (textChanged || layoutChanged) {
            layoutCache.update(
                text = text,
                style = style,
                fontFamilyResolver = fontFamilyResolver,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
            )
            if (isAttached) {
                invalidateMeasurement()
                invalidateDraw()
            }
        } else if (drawChanged && isAttached) {
            invalidateDraw()
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        // TODO MODIFY: flow-around support will need a different layout engine entry point here.
        layoutCache.density = this
        val didChangeLayout = layoutCache.layoutWithConstraints(constraints, layoutDirection)
        val paragraph = layoutCache.paragraph!!
        val layoutSize = layoutCache.layoutSize

        if (didChangeLayout) {
            invalidateLayer()
            @Suppress("PrimitiveInCollection") var cache = baselineCache
            if (cache == null) {
                cache = HashMap(2)
                baselineCache = cache
            }
            cache[FirstBaseline] = paragraph.firstBaseline.fastRoundToInt()
            cache[LastBaseline] = paragraph.lastBaseline.fastRoundToInt()
            onTextLayout?.invoke(layoutCache.createTextLayoutResult())
        }

        val placeable =
            measurable.measure(
                fitPrioritizingWidth(
                    minWidth = layoutSize.width,
                    maxWidth = layoutSize.width,
                    minHeight = layoutSize.height,
                    maxHeight = layoutSize.height,
                ),
            )

        return layout(layoutSize.width, layoutSize.height, baselineCache ?: emptyMap()) {
            placeable.place(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int {
        layoutCache.density = this
        return layoutCache.minIntrinsicWidth(layoutDirection)
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int {
        layoutCache.density = this
        return layoutCache.intrinsicHeight(width, layoutDirection)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int {
        layoutCache.density = this
        return layoutCache.maxIntrinsicWidth(layoutDirection)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int {
        layoutCache.density = this
        return layoutCache.intrinsicHeight(width, layoutDirection)
    }

    override fun ContentDrawScope.draw() {
        if (!isAttached) return

        // TODO MODIFY: drawing may need to switch from one Paragraph to multiple flow fragments.
        val localParagraph = layoutCache.paragraph ?: return
        drawIntoCanvas { canvas ->
            if (layoutCache.didOverflow) {
                val size = layoutCache.layoutSize
                canvas.save()
                canvas.clipRect(0f, 0f, size.width.toFloat(), size.height.toFloat())
            }
            try {
                val textDecoration = style.textDecoration ?: TextDecoration.None
                val shadow = style.shadow ?: Shadow.None
                val drawStyle = style.drawStyle ?: Fill
                val brush = style.brush
                if (brush != null) {
                    localParagraph.paint(
                        canvas = canvas,
                        brush = brush,
                        alpha = style.alpha,
                        shadow = shadow,
                        drawStyle = drawStyle,
                        textDecoration = textDecoration,
                    )
                } else {
                    val color =
                        when {
                            overrideColor.isSpecified -> overrideColor
                            style.color.isSpecified -> style.color
                            else -> Color.Black
                        }
                    localParagraph.paint(
                        canvas = canvas,
                        color = color,
                        shadow = shadow,
                        drawStyle = drawStyle,
                        textDecoration = textDecoration,
                    )
                }
            } finally {
                if (layoutCache.didOverflow) {
                    canvas.restore()
                }
            }
        }
    }
}
