package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow

internal class MyTextStringSimpleElement(
    private val text: String,
    private val style: TextStyle,
    private val fontFamilyResolver: FontFamily.Resolver,
    private val overflow: TextOverflow = TextOverflow.Clip,
    private val softWrap: Boolean = true,
    private val maxLines: Int = Int.MAX_VALUE,
    private val minLines: Int = 1,
    private val color: Color = Color.Unspecified,
    private val onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) : ModifierNodeElement<MyTextStringSimpleNode>() {

    override fun create(): MyTextStringSimpleNode =
        MyTextStringSimpleNode(
            text = text,
            style = style,
            fontFamilyResolver = fontFamilyResolver,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            overrideColor = color,
            onTextLayout = onTextLayout,
        )

    override fun update(node: MyTextStringSimpleNode) {
        node.update(
            text = text,
            style = style,
            fontFamilyResolver = fontFamilyResolver,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            overrideColor = color,
            onTextLayout = onTextLayout,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        // Show nothing in the inspector.
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MyTextStringSimpleElement) return false

        return text == other.text &&
                style == other.style &&
                fontFamilyResolver == other.fontFamilyResolver &&
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
        result = 31 * result + overflow.hashCode()
        result = 31 * result + softWrap.hashCode()
        result = 31 * result + maxLines
        result = 31 * result + minLines
        result = 31 * result + color.hashCode()
        result = 31 * result + (onTextLayout?.hashCode() ?: 0)
        return result
    }
}
