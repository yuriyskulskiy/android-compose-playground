package com.skul.yuriy.composeplayground.feature.metaballPrimer.text

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold10PercentEffect
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold20PercentEffect
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold30PercentEffect
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold50PercentEffect
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold5PercentEffect
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold70PercentEffect
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.applyRenderEffect
import kotlinx.coroutines.launch

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun TextMeltScreen(
    modifier: Modifier = Modifier,
) {
    val days = listOf(
        "Monday",
//        "1",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
    )
    var index by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }
    val blurMaxDp = 16.dp
    val blurAnim = remember { Animatable(0.dp, Dp.VectorConverter) }
    val scope = rememberCoroutineScope()
    val animationDurationMs = 650

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = days[index],
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(12.dp)
                )
                .animateContentSize(
//                    animationSpec = tween(durationMillis = 3000)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
//                .applyRenderEffect(alphaThreshold50PercentEffect)
//                .applyRenderEffect(alphaThreshold30PercentEffect)
//                .applyRenderEffect(alphaThreshold20PercentEffect)
//                .applyRenderEffect(alphaThreshold20PercentEffect)
                .applyRenderEffect(alphaThreshold5PercentEffect)

//                .applyRenderEffect(alphaThreshold5PercentEffect)
                .blur(blurAnim.value, edgeTreatment = BlurredEdgeTreatment.Unbounded),
        )

        Text(
            text = days[index],
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
                .applyRenderEffect(alphaThreshold20PercentEffect)
                .blur(blurAnim.value*2f),
        )

        Text(
            text = days[index],
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
//                .applyRenderEffect(alphaThreshold50PercentEffect)
                .applyRenderEffect(alphaThreshold70PercentEffect)
                .blur(blurAnim.value / 4f),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                onClick = {
                    if (isAnimating) return@Button
                    scope.launch {
                        isAnimating = true
                        blurAnim.animateTo(blurMaxDp, animationSpec = tween(animationDurationMs))
                        index = if (index == 0) days.lastIndex else index - 1
                        blurAnim.animateTo(0.dp, animationSpec = tween(animationDurationMs))
                        isAnimating = false
                    }
                }
            ) {
                Text(text = "Previous")
            }
            Button(
                onClick = {
                    if (isAnimating) return@Button
                    scope.launch {
                        isAnimating = true
                        blurAnim.animateTo(blurMaxDp, animationSpec = tween(animationDurationMs))
                        index = if (index == days.lastIndex) 0 else index + 1
                        blurAnim.animateTo(0.dp, animationSpec = tween(animationDurationMs))
                        isAnimating = false
                    }
                }
            ) {
                Text(text = "Next")
            }
        }
    }
}
