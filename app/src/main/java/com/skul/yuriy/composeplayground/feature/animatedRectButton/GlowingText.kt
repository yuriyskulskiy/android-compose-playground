package com.skul.yuriy.composeplayground.feature.animatedRectButton

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.applyRenderEffect
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold5PercentEffect

@Composable
fun GlowingText(
    text: String,
    textColor: Color,
    textSizeSp: Int,
    isPressed: Boolean,
    isMorphActive: Boolean,
    shadowOffset: Pair<Dp, Dp>,
    blurRadius: Dp
) {
    val morphBlur = remember { Animatable(0.dp, Dp.VectorConverter) }
    var displayText by remember(text) { mutableStateOf(text) }
    val morphDurationMs = 1200
    val morphPeakBlur = 16.dp

    LaunchedEffect(text) {
        displayText = text
    }

    LaunchedEffect(isMorphActive, text) {
        val targetText = if (isMorphActive) "RELEASE" else text
        if (displayText == targetText) {
            if (morphBlur.value > 0.dp) {
                morphBlur.animateTo(
                    targetValue = 0.dp,
                    animationSpec = tween(durationMillis = morphDurationMs / 2, easing = LinearEasing)
                )
            }
            return@LaunchedEffect
        }

        morphBlur.animateTo(
            targetValue = morphPeakBlur,
            animationSpec = tween(durationMillis = morphDurationMs / 2, easing = LinearEasing)
        )
        displayText = targetText
        morphBlur.animateTo(
            targetValue = 0.dp,
            animationSpec = tween(durationMillis = morphDurationMs / 2, easing = LinearEasing)
        )
    }

    val isMorphRenderingActive = morphBlur.value > 0.dp
    val morphModifier = if (isMorphRenderingActive) {
        val thresholdModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier.applyRenderEffect(effect = alphaThreshold5PercentEffect, clip = false)
        } else {
            Modifier
        }
        thresholdModifier
            .blur(
                radius = morphBlur.value,
                edgeTreatment = BlurredEdgeTreatment.Unbounded
            )
            .padding(horizontal = 4.dp, vertical = 16.dp)
    } else {
        Modifier
    }
    val shouldShowGlowShadow = !isPressed &&
        !isMorphActive &&
        !isMorphRenderingActive &&
        displayText == text

    Box(
        modifier = morphModifier
    ) {
        if (shouldShowGlowShadow) {
            Text(
                text = displayText,
                modifier = Modifier
                    .offset(
                        x = shadowOffset.first,
                        y = shadowOffset.second
                    )
                    .blur(blurRadius),
                color = textColor.copy(alpha = 0.8f),
                fontSize = textSizeSp.sp
            )
        }

        Text(
            text = displayText,
            color = textColor,
            fontSize = textSizeSp.sp
        )
    }
}
