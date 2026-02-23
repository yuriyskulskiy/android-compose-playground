package com.skul.yuriy.composeplayground.feature.liquidBar.prototype;

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LiquidBarContent_old(
    paddingValues:PaddingValues,
    contentText: String,
    modifier: Modifier = Modifier,
        ) {
    Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray)
                    .padding(paddingValues)
    ) {

        Wave1DLike(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .height(240.dp)

                        .border(1.dp, Color.Red),
                samples = 1024,
//            amp = 2.5f,
                amp = 0.5f,
//            yGain = 25f,
                yGain = 18f,
                pulseWidthNorm = 0.18f,
//            plotWidth = 8f,
                plotWidth = 1f,
                damping = 0.98f,
//            damping = 0.99f,
        )


    }
}
