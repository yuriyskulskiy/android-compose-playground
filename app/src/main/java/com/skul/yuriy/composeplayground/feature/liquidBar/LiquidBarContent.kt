package com.skul.yuriy.composeplayground.feature.liquidBar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun LiquidBarContent(
    screenMode: ScreenMode,
    clipContentByWavePath: Boolean,
    paddingValues: PaddingValues,
    contentText: String,
    modifier: Modifier = Modifier,
) {
    val useCanvasDifference = screenMode == ScreenMode.Canvas && !clipContentByWavePath

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (useCanvasDifference) {
                    Modifier
                        .zIndex(10f)
                        .invertByDifferenceBlend()
                        .padding(paddingValues)
                } else {
                    Modifier
                }
            )
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .then(
                    if (useCanvasDifference) {
                        Modifier
                    } else {
                        Modifier.padding(paddingValues)
                    }
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = contentText,
            color = if (useCanvasDifference) Color.White else Color.Black
        )
    }
}
