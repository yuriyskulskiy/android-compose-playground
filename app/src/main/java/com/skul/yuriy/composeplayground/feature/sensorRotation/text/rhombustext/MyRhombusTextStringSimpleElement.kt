package com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombustext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow

internal class MyRhombusTextStringSimpleElement(
    private val text: String,
    private val style: TextStyle,
    private val fontFamilyResolver: FontFamily.Resolver,
    private val config: RhombusTextLayoutConfig,
    private val overflow: TextOverflow = TextOverflow.Clip,
    private val softWrap: Boolean = true,
    private val maxLines: Int = Int.MAX_VALUE,
    private val minLines: Int = 1,
    private val color: Color = Color.Unspecified,
    private val onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) : ModifierNodeElement<MyRhombusTextStringSimpleNode>() {

    override fun create(): MyRhombusTextStringSimpleNode =
        MyRhombusTextStringSimpleNode(
            text = text,
            style = style,
            fontFamilyResolver = fontFamilyResolver,
            config = config,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            overrideColor = color,
            onTextLayout = onTextLayout,
        )

    override fun update(node: MyRhombusTextStringSimpleNode) {
        node.update(
            text = text,
            style = style,
            fontFamilyResolver = fontFamilyResolver,
            config = config,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            overrideColor = color,
            onTextLayout = onTextLayout,
        )
    }

    override fun InspectorInfo.inspectableProperties() = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MyRhombusTextStringSimpleElement) return false

        return text == other.text &&
            style == other.style &&
            fontFamilyResolver == other.fontFamilyResolver &&
            config == other.config &&
            overflow == other.overflow &&
            softWrap == other.softWrap &&
            maxLines == other.maxLines &&
            minLines == other.minLines &&
            color == other.color &&
            onTextLayout === other.onTextLayout
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + style.hashCode()
        result = 31 * result + fontFamilyResolver.hashCode()
        result = 31 * result + config.hashCode()
        result = 31 * result + overflow.hashCode()
        result = 31 * result + softWrap.hashCode()
        result = 31 * result + maxLines
        result = 31 * result + minLines
        result = 31 * result + color.hashCode()
        result = 31 * result + (onTextLayout?.hashCode() ?: 0)
        return result
    }
}
