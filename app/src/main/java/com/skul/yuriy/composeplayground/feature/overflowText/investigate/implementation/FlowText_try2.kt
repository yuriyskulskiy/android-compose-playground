package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
    val config = FlowTextDefaults.floatingBoxConfig()

    FlowText(
        text = "$text\n\n$text",
        config = config,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                    ),
        )
    }
}
