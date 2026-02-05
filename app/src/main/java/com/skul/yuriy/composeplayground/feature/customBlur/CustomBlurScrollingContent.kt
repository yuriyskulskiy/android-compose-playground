package com.skul.yuriy.composeplayground.feature.customBlur

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

@Composable
fun CustomBlurScrollingContent(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val text = stringResource(R.string.very_long_mock_text).trimIndent()

    Column(
        modifier = modifier
            .blur(8.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            text = text,
            fontWeight = FontWeight.Normal
        )
    }
}
