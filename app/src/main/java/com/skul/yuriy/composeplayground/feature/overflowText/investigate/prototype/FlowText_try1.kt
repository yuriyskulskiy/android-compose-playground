package com.skul.yuriy.composeplayground.feature.overflowText.investigate.prototype

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

@Composable
internal fun FlowText_try1(
    modifier: Modifier = Modifier,
) {
    val text = stringResource(R.string.very_long_mock_text_paragraphs)

    FlowTextPrototype(
        text = "$text\n\n$text",
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        floatingBoxSize = DpSize(width = 100.dp, height = 135.dp),
        floatingBoxGap = 16.dp,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                ),
        )
    }
}
