package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
internal fun MyBasicText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
) {
    val resolvedStyle = if (color == Color.Unspecified) {
        style
    } else {
        style.copy(color = color)
    }

    BasicText(
        text = text,
        modifier = modifier,
        style = resolvedStyle,
    )
}
