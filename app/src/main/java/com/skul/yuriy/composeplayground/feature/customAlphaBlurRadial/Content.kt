package com.skul.yuriy.composeplayground.feature.customAlphaBlurRadial

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.customAlphaBlurRadial.effects.dynamic.alphaGaussianBlurLocalRadialDynamic

@Composable
internal fun Content(
    centerMaxMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val content = contentText()

    Column(
        modifier = modifier
            .alphaGaussianBlurLocalRadialDynamic(
                radius = 12.dp,
                clearCenterRadius = 28.dp,
                centerZoneHasMaxBlur = centerMaxMode,
                color = Color.Black.copy(alpha = 0.95f)
            )
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = content,
            modifier = Modifier.padding(vertical = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun contentText(): String {
    val longText = stringResource(R.string.very_long_mock_text)
        .replace('\n', ' ')
        .replace(Regex("\\s+"), " ")
        .trim()
    return "$longText $longText"
}
