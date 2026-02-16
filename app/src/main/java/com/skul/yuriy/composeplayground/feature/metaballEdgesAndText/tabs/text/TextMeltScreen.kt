package com.skul.yuriy.composeplayground.feature.metaballEdgesAndText.tabs.text

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold20PercentEffect
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold5PercentEffect
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold70PercentEffect
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.applyRenderEffect

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun TextMeltScreen(
    state: TextMeltState,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceAround,
    ) {

        val multiplierDouble = 2f
        val multiplierQuarter = 0.25f
        val maxBlurBase = state.blurMaxDp


        MeltExampleItem(
            text = state.currentDay,
            blurRadius = state.blur,
            maxBlur = maxBlurBase,
            alphaPercent = 5,
            alphaEffect = alphaThreshold5PercentEffect,
        )
        MeltExampleItem(
            text = state.currentDay,
            blurRadius = state.blur * multiplierDouble,
            maxBlur = maxBlurBase * multiplierDouble,
            alphaPercent = 20,
            alphaEffect = alphaThreshold20PercentEffect,
        )
        MeltExampleItem(
            text = state.currentDay,
            blurRadius = state.blur * multiplierQuarter,
            maxBlur = maxBlurBase * multiplierQuarter,
            alphaPercent = 70,
            alphaEffect = alphaThreshold70PercentEffect,
        )
    }
}

private fun buildDescription(maxBlur: Dp, alphaPercent: Int): AnnotatedString {
    return buildAnnotatedString {
        append("Animation: blur radius from 0 to ")
        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
            append("${maxBlur.value.toInt()}.dp")
        }
        append("; alpha filter = ")
        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
            append("$alphaPercent%")
        }
    }
}

@Composable
private fun MeltExampleItem(
    text: String,
    blurRadius: Dp,
    maxBlur: Dp,
    alphaPercent: Int,
    alphaEffect: RenderEffect,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(percent = 12)
                )
                .animateContentSize(animationSpec = tween(durationMillis = 300))
                .applyRenderEffect(alphaEffect, clip = false)
                .blur(radius = blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = buildDescription(maxBlur = maxBlur, alphaPercent = alphaPercent),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
