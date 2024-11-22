package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.legacy

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.outlineGradient

//Box with  or gradient modifier just for testing blur/gradient border on old devices
@Composable
fun BlurredCircleWithBorder(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 28.dp,
    blurColor: Color = Color.Black
) {
    Box(
        modifier = modifier
            .size(112.dp)
            .border(width = 1.dp, Color.Red, CircleShape)
//            .outlineBlur(blurRadius, shape = CircleShape, color = blurColor)
            .outlineGradient(gradientRadiusOffset = blurRadius, color = blurColor)
    ) {
        //empty content
    }
}

