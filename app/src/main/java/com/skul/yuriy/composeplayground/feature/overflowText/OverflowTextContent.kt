package com.skul.yuriy.composeplayground.feature.overflowText

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.modifier.ModifierLocal
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

@Composable
internal fun OverflowTextContent(
    modifier: Modifier = Modifier,
) {
    val text = stringResource(R.string.very_long_mock_text_paragraphs)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {

        Spacer(Modifier.size(20.dp))
        Box(
            Modifier
                .width(100.dp)
                .height(135.dp)
                .border(color = MaterialTheme.colorScheme.onSurfaceVariant, width = 1.dp)
        ) {
        }

        Spacer(Modifier.size(20.dp))
        Text(
            text = "$text\n\n$text",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
