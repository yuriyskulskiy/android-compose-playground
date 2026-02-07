package com.skul.yuriy.composeplayground.feature.metaballPrimer.text

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold20PercentEffect
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold5PercentEffect
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold70PercentEffect
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.applyRenderEffect

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun TextMeltScreen(
    state: TextMeltState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
            Text(
                text = state.currentDay,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .animateContentSize()
                    .applyRenderEffect(alphaThreshold5PercentEffect, clip = false)
                    .blur(state.blur, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "Animation: blur radius from 0 to 16.dp; alpha filter = 5%",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = state.currentDay,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 300)
                    )
                    .applyRenderEffect(alphaThreshold20PercentEffect, clip = false)
                    .blur(state.blur * 2f),
            )
            Text(
                text = "Animation: blur radius from 0 to 32.dp; alpha filter = 20%",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = state.currentDay,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 300)
                    )
                    .applyRenderEffect(alphaThreshold70PercentEffect, clip = false)
                    .blur(state.blur / 4f),
            )
            Text(
                text = "Animation: blur radius from 0 to 4.dp; alpha filter = 70%",
                style = MaterialTheme.typography.bodyMedium,
            )
    }
}
