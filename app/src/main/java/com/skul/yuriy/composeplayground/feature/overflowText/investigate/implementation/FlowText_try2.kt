package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

@Composable
internal fun FlowText_try2(
    modifier: Modifier = Modifier,
) {
    val text = stringResource(R.string.very_long_mock_text_paragraphs)
    val config =
        FloatingBoxConfig(
            width = 100.dp,
            height = 135.dp,
            topOffset = 56.dp,
            gap = 16.dp,
        )

    FlowText(
        text = "$text\n\n$text",
        config = config,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
    )
}
