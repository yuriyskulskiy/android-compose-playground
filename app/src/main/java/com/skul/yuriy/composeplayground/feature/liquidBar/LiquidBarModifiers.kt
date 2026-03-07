package com.skul.yuriy.composeplayground.feature.liquidBar

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import android.os.SystemClock
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

//internal fun Modifier.invertByDifferenceBlend(): Modifier = graphicsLayer {
//    compositingStrategy = CompositingStrategy.Offscreen
//    blendMode = BlendMode.Difference
//}

internal fun Modifier.invertByDifferenceBlend(): Modifier = this

