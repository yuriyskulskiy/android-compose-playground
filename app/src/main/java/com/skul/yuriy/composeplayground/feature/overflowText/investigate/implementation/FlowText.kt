package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
internal fun FlowText(
    text: String,
    config: FloatingBoxConfig,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        MyBasicText(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = style,
            config = config,
        )

        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = config.topOffset)
                    .size(width = config.width, height = config.height)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                    ),
        )
    }
}
