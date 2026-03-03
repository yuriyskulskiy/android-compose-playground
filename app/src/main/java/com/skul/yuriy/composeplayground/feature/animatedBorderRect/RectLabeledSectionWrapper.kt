package com.skul.yuriy.composeplayground.feature.animatedBorderRect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RectLabeledSectionWrapper(
    modifier: Modifier = Modifier,
    text: String,
    shadowBox: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(48.dp))
        shadowBox()
        Spacer(modifier = Modifier.size(24.dp))
        Text(text = text, color = Color.White)
    }
}
