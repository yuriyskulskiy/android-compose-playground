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
    paddingValues: PaddingValues,
    contentText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .then(
                when (screenMode) {
                    ScreenMode.Canvas -> Modifier
                        .zIndex(10f)
                        .invertByDifferenceBlend()
                        .padding(paddingValues)

                    ScreenMode.Agsl,
                    ScreenMode.AgslCanvas -> Modifier
                }
            )
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .then(
                    when (screenMode) {
                        ScreenMode.Canvas -> Modifier
                        ScreenMode.Agsl,
                        ScreenMode.AgslCanvas -> Modifier.padding(paddingValues)
                    }
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = contentText,
            color = when (screenMode) {
                ScreenMode.Canvas -> Color.White
                ScreenMode.Agsl,
                ScreenMode.AgslCanvas -> Color.Black
            }
        )
    }
}
