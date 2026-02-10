package com.skul.yuriy.composeplayground.feature.metaballEdgeText.text.concept

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.customBlur.util.rememberAlphaThresholdAgslEffect
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.applyRenderEffect

private const val ConceptPreviewCharCount = 371

@Composable
internal fun ConceptBlurTextContent(
    blurRadiusDp: Float,
    blurEnabled: Boolean,
    alphaEnabled: Boolean,
    alphaFilterPercent: Float,
    modifier: Modifier = Modifier,
) {
    val conceptText = stringResource(R.string.very_long_mock_text)
        .trimIndent()
        .trim('"')
        .take(ConceptPreviewCharCount)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        MetaballText(
            text = conceptText,
            modifier = Modifier.fillMaxWidth(),
            blurRadiusDp = blurRadiusDp,
            blurEnabled = blurEnabled,
            alphaEnabled = alphaEnabled,
            alphaFilterPercent = alphaFilterPercent
        )
    }
}

@Composable
private fun MetaballText(
    text: String,
    blurRadiusDp: Float,
    blurEnabled: Boolean,
    alphaEnabled: Boolean,
    alphaFilterPercent: Float,
    modifier: Modifier = Modifier,
) {
    val alphaThreshold01 = (alphaFilterPercent / 100f).coerceIn(0f, 1f)
    val alphaEffect = rememberAlphaThresholdAgslEffect(alphaThreshold01)

    Box(
        modifier = modifier
            .wrapContentSize()
            .then(
                if (alphaEnabled && alphaEffect != null) {
                    Modifier.applyRenderEffect(effect = alphaEffect, clip = true)
                } else {
                    Modifier
                }
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .then(
                    if (blurEnabled) {
                        Modifier.blur(radius = blurRadiusDp.dp)
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.Black
        )
    }
}
