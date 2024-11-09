package com.skul.yuriy.composeplayground.feature.animatedCircularButton

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedCircleButtonScreenContent(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
    ) {

        Text(
            color = Color.White,
            text = "Circular btn"
        )
    }
}