package com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombustext

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
 * Project-owned text node that lays out one paragraph fragment per visual line and draws each line
 * with its own horizontal offset.
 */
internal class MyRhombusTextStringSimpleNode(
    private var text: String,
    private var style: TextStyle,
    private var fontFamilyResolver: FontFamily.Resolver,
    private var config: RhombusTextLayoutConfig,
    private var overflow: TextOverflow = TextOverflow.Clip,
    private var softWrap: Boolean = true,
    private var maxLines: Int = Int.MAX_VALUE,
    private var minLines: Int = 1,
    private var overrideColor: Color = Color.Unspecified,
    private var onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) : Modifier.Node(), LayoutModifierNode, DrawModifierNode {

    @Suppress("PrimitiveInCollection")
    private var baselineCache: MutableMap<AlignmentLine, Int>? = null

    private val layoutCache: MyRhombusParagraphLayoutCache by lazy {
        MyRhombusParagraphLayoutCache(
            text = text,
            style = style,
            fontFamilyResolver = fontFamilyResolver,
            config = config,
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
        config: RhombusTextLayoutConfig,
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
                this.config != config ||
                this.overflow != overflow ||
                this.softWrap != softWrap ||
                this.maxLines != maxLines ||
                this.minLines != minLines
        val drawChanged =
            this.overrideColor != overrideColor || this.style != style || this.onTextLayout !== onTextLayout

        this.text = text
        this.style = style
        this.fontFamilyResolver = fontFamilyResolver
        this.config = config
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
                config = config,
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
        layoutCache.density = this
        val didChangeLayout = layoutCache.layoutWithConstraints(constraints, layoutDirection)
        val firstFragment = layoutCache.fragments.first()
        val lastFragment = layoutCache.fragments.last()
        val layoutSize = layoutCache.layoutSize

        if (didChangeLayout) {
            invalidateLayer()
            @Suppress("PrimitiveInCollection")
            var cache = baselineCache
            if (cache == null) {
                cache = HashMap(2)
                baselineCache = cache
            }
            cache[FirstBaseline] =
                firstFragment.offsetY + firstFragment.paragraph.firstBaseline.fastRoundToInt()
            cache[LastBaseline] =
                lastFragment.offsetY + lastFragment.paragraph.lastBaseline.fastRoundToInt()
            onTextLayout?.invoke(layoutCache.createTextLayoutResult())
        }

        val placeable =
            measurable.measure(
                fitPrioritizingWidth(
                    minWidth = layoutSize.width,
                    maxWidth = layoutSize.width,
                    minHeight = layoutSize.height,
                    maxHeight = layoutSize.height,
                )
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
        val fragments = layoutCache.fragments
        if (fragments.isEmpty()) return

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
                val color =
                    when {
                        overrideColor.isSpecified -> overrideColor
                        style.color.isSpecified -> style.color
                        else -> Color.Black
                    }

                fragments.forEach { fragment ->
                    canvas.save()
                    canvas.translate(fragment.offsetX.toFloat(), fragment.offsetY.toFloat())
                    if (brush != null) {
                        fragment.paragraph.paint(
                            canvas = canvas,
                            brush = brush,
                            alpha = style.alpha,
                            shadow = shadow,
                            drawStyle = drawStyle,
                            textDecoration = textDecoration,
                        )
                    } else {
                        fragment.paragraph.paint(
                            canvas = canvas,
                            color = color,
                            shadow = shadow,
                            drawStyle = drawStyle,
                            textDecoration = textDecoration,
                        )
                    }
                    canvas.restore()
                }
            } finally {
                if (layoutCache.didOverflow) {
                    canvas.restore()
                }
            }
        }
    }
}
